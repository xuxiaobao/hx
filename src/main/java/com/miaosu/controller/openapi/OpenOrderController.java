package com.miaosu.controller.openapi;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miaosu.Page;
import com.miaosu.base.ConstantsUtils;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.mapper.UserDiscountMapper;
import com.miaosu.model.Balance;
import com.miaosu.model.FlowRechargeInfo;
import com.miaosu.model.Member;
import com.miaosu.model.Order;
import com.miaosu.model.OrderType;
import com.miaosu.model.Product;
import com.miaosu.model.ProductDetail;
import com.miaosu.model.User;
import com.miaosu.model.UserDiscount;
import com.miaosu.model.enums.PayState;
import com.miaosu.model.enums.RechargeState;
import com.miaosu.monitor.MonitorService;
import com.miaosu.service.acct.AcctService;
import com.miaosu.service.balance.BalanceService;
import com.miaosu.service.huazong.HuaZongPlatform;
import com.miaosu.service.members.MemberService;
import com.miaosu.service.orders.AbstractOrderService;
import com.miaosu.service.products.ProductService;
import com.miaosu.service.serialno.SerialNoUtil;
import com.miaosu.service.user.UserService;
import com.miaosu.util.DESUtil;
import com.miaosu.util.DefaultUtil;

/**
 * 订单开放接口
 */
@RestController
@RequestMapping("/openapi/order")
public class OpenOrderController extends OpenBaseController {
	private static Logger logger = LoggerFactory.getLogger(OpenOrderController.class);

	@Resource(name = "simpleOrderService")
	private AbstractOrderService abstractOrderService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private UserService userService;

	@Autowired
	private BalanceService balanceService;

	@Autowired
	private SerialNoUtil serialNoUtil;

	@Autowired
	private AcctService acctService;

	@Autowired
	private HuaZongPlatform huaZongPlatform;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserDiscountMapper userDiscountMapper;
	
	@Autowired
	private MonitorService monitorService;
	
	@Value("${monitor.alarmbalance}")
	private String alarmBalance;

	@RequestMapping(value = "create", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResultInfo<Map<String, Object>> create(@RequestParam(value = "userId") final String userId,
			@RequestParam(value = "phone") final String phone,
			@RequestParam(value = "type", required = false) final Integer type,
			@RequestParam(value = "productId") final String productId,
			@RequestParam(value = "transId") final String transId,
			@RequestParam(value = "province") final String province,
			@RequestParam(value = "notifyUrl", required = false) final String notifyUrl,
			@RequestParam(value = "sign") final String sign) {
		Map<String, Object> data = new HashMap<>();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", userId);
		paramMap.put("phone", phone);
		paramMap.put("type", type);
		paramMap.put("productId", productId);
		paramMap.put("transId", transId);
		paramMap.put("province", province);
		paramMap.put("notifyUrl", notifyUrl);
		
		
		//禁止同一手机号90s内提交多次订单
		List<Order> orderList = abstractOrderService.findTheSamePhoneNumber(phone);
		if(orderList!=null&&orderList.size()>0){
			data.put("phone", orderList.get(0).getId());
			logger.warn("300秒内重复下单{}", orderList.get(0).getId());
			return new ResultInfo<>(true, ResultCode.SAME_PHONE_NUMBER_ILLEGAL_BILLING, data);
		}
		
		// Step.1 签名校验
		Member member = memberService.get(userId);
		User user = userService.get(userId);
		if (member == null || user == null || !user.isEnabled()) {
			// 用户不存在或被禁用
			throw new ServiceException(ResultCode.OPEN_USER_NOT_EXISTS);
		}
		checkSign("/openapi/order/create", paramMap, sign, DESUtil.decryptToString(member.getToken(), userId));

		// Step.2 订单是否已存在
		Order order = abstractOrderService.findByUsernameAndExternalId(userId, transId);
		if (order != null) {
			// 幂等设计，订单存在时返回订单编号
			data.put("orderId", order.getId());
			logger.warn("重复下单{}", order.getId());
			return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
		}

		Order newOrder = new Order();
		newOrder.setProductId(productId);
		newOrder.setUsername(userId);
		newOrder.setEffectType(type);
		newOrder.setExternalId(transId);
		newOrder.setNotifyUrl(notifyUrl);
		newOrder.setProvince(province);
		newOrder.setCreateTime(new Date());
		newOrder.setRechargeState(RechargeState.INIT);
		newOrder.setOrderType(OrderType.LL.getCode());
		newOrder.setOperator(DefaultUtil.getOperator(phone));
		newOrder.setIsManual(0);//设置充值方式非手动 即自动
		
		Product product = abstractOrderService.matchProduct(productId);

		List<ProductDetail> productDetailList = product.getProductDetailList();

		String operator = "";
		if (CollectionUtils.isNotEmpty(productDetailList)) {
			for (ProductDetail productDetail : productDetailList) {
				if (StringUtils.equals(productDetail.getProName(), "运营商")) {
					operator = productDetail.getProValue();
				}
			}
		}

		UserDiscount userDiscount = null;
		if (StringUtils.isNotEmpty(operator)) {
			userDiscount = new UserDiscount();
			userDiscount.setUserName(userId);
			userDiscount.setPid(operator);
			userDiscount = userDiscountMapper.select(userDiscount);
		}

		BigDecimal bigDecimal = null;
		if (userDiscount == null || userDiscount.getDiscount() == null) {
			bigDecimal = member.getDiscount();
		} else {
			bigDecimal = new BigDecimal(userDiscount.getDiscount());
		}
		logger.info("productID:{},userName:{},折扣是：{}", productId, userId, bigDecimal);
		// 计算订单价格
		BigDecimal orderPrice = product.getPrice().multiply(bigDecimal).setScale(4, BigDecimal.ROUND_DOWN);

		// Step.4 余额校验
		Balance balance = balanceService.get(userId);
		if (balance == null || balance.getBalance().compareTo(orderPrice) < 0) {
			// 余额不足
			throw new ServiceException(ResultCode.OPEN_NO_BALANCE);
		}
		// Step.5 生成订单与支付流水
		String orderId = serialNoUtil.genrateOrderNo();
		String billId = serialNoUtil.genrateBillNo();
		if (orderId == null || billId == null) {
			logger.warn("生成订单号与流水单号失败");
			throw new ServiceException(ResultCode.FAILED);
		}

		newOrder.setId(orderId);
		newOrder.setPayId(billId);
		newOrder.setPayState(PayState.INIT);
		newOrder.setPhone(phone);
		newOrder.setPrice(orderPrice);
		newOrder.setProductName(product.getName());
		newOrder.setProductPrice(product.getPrice());

		// 设置充值信息
		FlowRechargeInfo flowRechargeInfo = productService.getFlowRechargeInfo(productDetailList);

		newOrder.setRechargeInfo(JSON.toJSONString(flowRechargeInfo));
		newOrder.setRechargeInfoObj(flowRechargeInfo);

		try {
			logger.info("收到订单充值请求,{}", newOrder);
			order = abstractOrderService.create(newOrder);
		} catch (ServiceException ex) {
			// 数据已存在
			if (ResultCode.DATA_EXISTS.equals(ex.getErrorCode())) {
				order = abstractOrderService.findByUsernameAndExternalId(userId, transId);
				if (order != null) {
					// 幂等设计，订单存在时返回订单编号
					data.put("orderId", order.getId());
					return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
				}
			} else {
				throw ex;
			}
		}
		if (order == null) {
			throw new ServiceException(ResultCode.FAILED);
		}

		// Step.6 支付订单
		try {
			// 0：成功；1：余额不足；2: 支付单号不存在；3: 支付单状态异常
			int payState = acctService.payment(userId, billId, orderId);
			logger.info("订单{}与支付单{}的支付处理结果：{}", orderId, billId, payState);
		} catch (Exception ex) {
			logger.warn("下单时支付失败，orderId:{}, billId:{}", orderId, billId, ex);
		}
		Balance newBalance = balanceService.get(userId);
		//余额告警
		logger.info("alarmbalance:{}",alarmBalance);
		if(alarmBalance.equals("true"))
		{
			try
			{
				monitorService.alarmBalance(balance.getBalance(), newBalance.getBalance(), member.getUsername(), member.getEmail());
			}
			catch(Exception e)
			{
				logger.warn("alarm balance(send mail) error:{}", e);
			}
		}
		
		data.put("orderId", order.getId());
		return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
	}

	@RequestMapping(value = "/miaosuCreate", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResultInfo<Map<String, Object>> miaosuCreate(
			@RequestParam(value = "userId", required = true) final String username,
			@RequestParam(value = "phone", required = true) final String phone,
			@RequestParam(value = "type", required = false) final Integer type,
			@RequestParam(value = "productId") final String productId,
			@RequestParam(value = "transId") final String transId,
			@RequestParam(value = "province") final String province, HttpServletRequest request) {
		Map<String, Object> data = new HashMap<>();

		User loginUser = (User) WebUtils.getSessionAttribute(request, ConstantsUtils.LOGIN_KEY);

		if (!loginUser.isAdmin() || StringUtils.isEmpty(phone) || type == null || StringUtils.isEmpty(productId)
				|| StringUtils.isEmpty(transId) || StringUtils.isEmpty(province)) {
			return new ResultInfo<>(false, ResultCode.FAILED);
		}
		//String userId = "miaosu2015";
		String notifyUrl = "";
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", username);
		paramMap.put("phone", phone);
		paramMap.put("type", type);
		paramMap.put("productId", productId);
		paramMap.put("transId", transId);
		paramMap.put("province", province);
		paramMap.put("notifyUrl", notifyUrl);

		// Step.1 签名校验
		Member member = memberService.get(username);
		User user = userService.get(username);
		if (member == null || user == null || !user.isEnabled()) {
			// 用户不存在或被禁用
			throw new ServiceException(ResultCode.OPEN_USER_NOT_EXISTS);
		}
		// Step.2 订单是否已存在
		Order order = abstractOrderService.findByUsernameAndExternalId(username, transId);
		if (order != null) {
			// 幂等设计，订单存在时返回订单编号
			data.put("orderId", order.getId());
			logger.warn("重复下单{}", order.getId());
			return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
		}

		Order newOrder = new Order();
		newOrder.setProductId(productId);
		newOrder.setUsername(username);
		newOrder.setEffectType(type);
		newOrder.setExternalId(transId);
		newOrder.setNotifyUrl(notifyUrl);
		newOrder.setProvince(province);
		newOrder.setCreateTime(new Date());
		newOrder.setRechargeState(RechargeState.INIT);
		newOrder.setOrderType(OrderType.LL.getCode());
		newOrder.setOperator(DefaultUtil.getOperator(phone));
		newOrder.setIsManual(1);
		
		Product product = abstractOrderService.matchProduct(productId);

		List<ProductDetail> productDetailList = product.getProductDetailList();
		
		String operator = "";
		if (CollectionUtils.isNotEmpty(productDetailList)) {
			for (ProductDetail productDetail : productDetailList) {
				if (StringUtils.equals(productDetail.getProName(), "运营商")) {
					operator = productDetail.getProValue();
				}
			}
		}
		
		UserDiscount userDiscount = null;
		if (StringUtils.isNotEmpty(operator)) {
			userDiscount = new UserDiscount();
			userDiscount.setUserName(username);
			userDiscount.setPid(operator);
			userDiscount = userDiscountMapper.select(userDiscount);
		}
		
		BigDecimal bigDecimal = null;
		if (userDiscount == null || userDiscount.getDiscount() == null) {
			bigDecimal = member.getDiscount();
		} else {
			bigDecimal = new BigDecimal(userDiscount.getDiscount());
		}
		logger.info("productID:{},userName:{},折扣是：{}", productId, username, bigDecimal);
		// 计算订单价格
		BigDecimal orderPrice = product.getPrice().multiply(bigDecimal).setScale(4, BigDecimal.ROUND_DOWN);

		// Step.4 余额校验
		Balance balance = balanceService.get(username);
		if (balance == null || balance.getBalance().compareTo(orderPrice) < 0) {
			// 余额不足
			throw new ServiceException(ResultCode.OPEN_NO_BALANCE);
		}

		// Step.5 生成订单与支付流水
		String orderId = serialNoUtil.genrateOrderNo();
		String billId = serialNoUtil.genrateBillNo();
		if (orderId == null || billId == null) {
			logger.warn("生成订单号与流水单号失败");
			throw new ServiceException(ResultCode.FAILED);
		}

		newOrder.setId(orderId);
		newOrder.setPayId(billId);
		newOrder.setPayState(PayState.INIT);
		newOrder.setPhone(phone);
		newOrder.setPrice(orderPrice);
		newOrder.setProductName(product.getName());
		newOrder.setProductPrice(product.getPrice());

		FlowRechargeInfo flowRechargeInfo = productService.getFlowRechargeInfo(productDetailList);

		newOrder.setRechargeInfo(JSON.toJSONString(flowRechargeInfo));
		newOrder.setRechargeInfoObj(flowRechargeInfo);
		try {
			order = abstractOrderService.create(newOrder);
		} catch (ServiceException ex) {
			// 数据已存在
			if (ResultCode.DATA_EXISTS.equals(ex.getErrorCode())) {
				order = abstractOrderService.findByUsernameAndExternalId(username, transId);
				if (order != null) {
					// 幂等设计，订单存在时返回订单编号
					data.put("orderId", order.getId());
					return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
				}
			} else {
				throw ex;
			}
		}
		if (order == null) {
			throw new ServiceException(ResultCode.FAILED);
		}

		// Step.6 支付订单
		try {
			// 0：成功；1：余额不足；2: 支付单号不存在；3: 支付单状态异常
			int payState = acctService.payment(username, billId, orderId);
			logger.info("订单{}与支付单{}的支付处理结果：{}", orderId, billId, payState);
		} catch (Exception ex) {
			logger.warn("下单时支付失败，orderId:{}, billId:{}", orderId, billId, ex);
		}

		data.put("orderId", order.getId());
		return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);

	}

	@RequestMapping(value = "validate", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResultInfo validate(@RequestParam(value = "userId") final String userId,
			@RequestParam(value = "phone") final String phone,
			@RequestParam(value = "productId") final String productId,
			@RequestParam(value = "sign") final String sign) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", userId);
		paramMap.put("phone", phone);
		paramMap.put("productId", productId);

		// Step.1 签名校验
		Member member = memberService.get(userId);
		User user = userService.get(userId);
		if (member == null || user == null || !user.isEnabled()) {
			// 用户不存在或被禁用
			throw new ServiceException(ResultCode.OPEN_USER_NOT_EXISTS);
		}
		checkSign("/openapi/order/validate", paramMap, sign, DESUtil.decryptToString(member.getToken(), userId));

		// Step.2 检验能否充值
		try {
			boolean result = huaZongPlatform.validate(phone, productId);
			return result ? new ResultInfo(true, ResultCode.SUCCESSFUL) : new ResultInfo(false, ResultCode.FAILED);
		} catch (Exception ex) {
			logger.warn("{}校验{}能否充值失败, exMsg:{}", phone, productId, ex.getMessage());
			return new ResultInfo(false, ResultCode.FAILED, ex.getMessage());
		}
	}

	@RequestMapping(value = "status", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResultInfo<Map<String, Object>> status(@RequestParam(value = "userId") final String userId,
			@RequestParam(value = "orderId") final String orderId, @RequestParam(value = "sign") final String sign) {
		Map<String, Object> data = new HashMap<>();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", userId);
		paramMap.put("orderId", orderId);

		// Step.1 签名校验
		Member member = memberService.get(userId);
		User user = userService.get(userId);
		if (member == null || user == null || !user.isEnabled()) {
			// 用户不存在或被禁用
			throw new ServiceException(ResultCode.OPEN_USER_NOT_EXISTS);
		}
		checkSign("/openapi/order/status", paramMap, sign, DESUtil.decryptToString(member.getToken(), userId));

		// Step.2 查询订单信息
		RechargeState rechargeState = null;

		// 从数据库获取
		Order order = abstractOrderService.findByIdAndUsername(orderId, userId);

		if(order == null)
		{
			return new ResultInfo<>(true, ResultCode.DATA_NOT_EXISTS);
		}
		// 为空时返回充值中
		rechargeState = (order.getRechargeState() == null ? RechargeState.PROCESS : order.getRechargeState());

		data.put("orderId", order.getId());
		data.put("status", rechargeState.getOper());
		data.put("remark", order.getRechargeFailedReason());
		return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
	}

	@RequestMapping(value = "queryOrderId", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResultInfo<Map<String, Object>> query(@RequestParam(value = "userId") final String userId,
			@RequestParam(value = "transId") final String transId, @RequestParam(value = "sign") final String sign) {
		Map<String, Object> data = new HashMap<>();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", userId);
		paramMap.put("transId", transId);

		// Step.1 签名校验
		Member member = memberService.get(userId);
		User user = userService.get(userId);
		if (member == null || user == null || !user.isEnabled()) {
			// 用户不存在或被禁用
			throw new ServiceException(ResultCode.OPEN_USER_NOT_EXISTS);
		}
		checkSign("/openapi/order/queryOrderId", paramMap, sign, DESUtil.decryptToString(member.getToken(), userId));

		Order order = abstractOrderService.findByUsernameAndExternalId(userId, transId);

		if (order == null) {
			throw new ServiceException(ResultCode.OPEN_ORDER_NOT_EXISTS);
		}

		data.put("orderId", order.getId());
		return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
	}

	/**
	 * 批量充值
	 * @param file
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "batch", method = { RequestMethod.GET, RequestMethod.POST })
	public void batch(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("text/html;charset=utf-8");
		int pos = file.getOriginalFilename().lastIndexOf(".");
		if(pos == -1)
		{
			//格式不支持
			try {
				response.getWriter().write(JSONObject
						.toJSONString(new ResultInfo<>(true, ResultCode.FAILED, "请选择要传的文件")));
				response.getWriter().flush();
				response.getWriter().close();
			} catch (IOException e) {
				logger.error("批量导入充值列表失败{}", e);
			}
			return;
		}
		String suffix = file.getOriginalFilename().substring(pos, file.getOriginalFilename().length()); 
		if(!suffix.equals(".xls") &&  !suffix.equals(".xlsx"))
		{
			//格式不支持
			try {
				response.getWriter().write(JSONObject
						.toJSONString(new ResultInfo<>(true, ResultCode.FAILED, "文件格式不正确")));
				response.getWriter().flush();
				response.getWriter().close();
			} catch (IOException e) {
				logger.error("批量导入充值列表失败{}", e);
			}
			return;
		}
		
		List<String> rechageList = new ArrayList<String>();
		List<Product> productList = productService.find(null, new Page(0, 1000)).getData();
		List<String> products = new ArrayList<String>();
		for (Product product : productList) {
			products.add(product.getId());
		}
		String result = "";

		POIFSFileSystem poiFile = null;
		Workbook workbook = null;
		DecimalFormat decimalFormat = new DecimalFormat("###0");
		try {
			if(suffix.equals(".xlsx"))
			{
				workbook = new XSSFWorkbook(file.getInputStream());
			}
			else
			{
				poiFile = new POIFSFileSystem(file.getInputStream());
				workbook = new HSSFWorkbook(poiFile);
			}
			Sheet sheet = workbook.getSheetAt(0);
			int start = sheet.getFirstRowNum() + 1;
			int last = sheet.getLastRowNum();
			Row row = null;
			for (int i = start; i <= last; i++) {
				row = sheet.getRow(i);
				String phone = decimalFormat.format(row.getCell(0).getNumericCellValue());
				if (NumberUtils.isNumber(phone) && phone.length() == 11) {
					String productId = row.getCell(1).getStringCellValue();
					if (StringUtils.isNotEmpty(productId) && products.contains(productId)) {
						if (rechageList.contains(phone + "|" + productId)) {
							result = JSONObject
									.toJSONString(new ResultInfo<>(true, ResultCode.FAILED, "第" + i + "数据重复"));
							break;
						} else {
							rechageList.add(phone + "|" + productId);
						}
					} else {
						result = JSONObject
								.toJSONString(new ResultInfo<>(true, ResultCode.FAILED, "第" + i + "产品不正确"));
						break;
					}
				} else {
					result = JSONObject.toJSONString(new ResultInfo<>(true, ResultCode.FAILED, "第" + i + "手机号码不正确"));
					break;
				}
			}

			logger.info("batch reachage list:{}", rechageList);
			if (StringUtils.isEmpty(result)) {
				result = JSONObject.toJSONString(new ResultInfo<>(true, ResultCode.SUCCESSFUL, "操作成功"));

				// batch recharege
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String transId = "";
				int randomNumber = 0;
				for (String item : rechageList) {
					randomNumber = RandomUtils.nextInt(999);
					randomNumber = randomNumber < 100 ? randomNumber + 100 : randomNumber;
					String[] s = item.split("\\|");
					transId = "S_N" + format.format(new Date()) + randomNumber;
					miaosuCreate("miaosu2015", s[0], 0, s[1], transId, "江苏省", request);
				}
			}
		} catch (IOException e) {
			logger.error("批量导入充值列表失败{}", e);
			result = JSONObject.toJSONString(new ResultInfo<>(true, ResultCode.FAILED, "批量导入充值列表异常"));
		}
		try {
			response.getWriter().write(result);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("获取response Writer{}", e);
		}
	}
	
	public void setAlarmBalance(String alarmBalance)
	{
		this.alarmBalance = alarmBalance;
	}
}
