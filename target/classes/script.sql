/* FUNCTIONS */;
DROP FUNCTION IF EXISTS `cur_val`;
DELIMITER $$
CREATE FUNCTION `cur_val`(v_seqName varchar(32)) RETURNS bigint(20)
begin
            declare value bigint;
            set value = 0;
            select cur_val into value from serial_no where seq_name = v_seqName;
            return value;
            end
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `next_val`;
DELIMITER $$
CREATE FUNCTION `next_val`(v_seqName varchar(32)) RETURNS bigint(20)
begin
            update serial_no set cur_val = cur_val + increment_val where seq_name = v_seqName;
            return cur_val(v_seqName);
            end
$$
DELIMITER ;

DROP FUNCTION IF EXISTS `stat_bills`;
DELIMITER $$
CREATE FUNCTION `stat_bills`(v_beginDate varchar(10), v_endDate varchar(10)) RETURNS int(11)
BEGIN
                DECLARE o_result int;
                set o_result = -1;

                DELETE FROM bill_stats WHERE stat_date >= v_beginDate and stat_date < v_endDate;

                INSERT INTO bill_stats (
                    stat_date,
                    username,
                    balance,
                    add_sum,
                    pay_sum,
                    refund_sum,
                    reward_sum,
                    others_sum
                ) SELECT 
                	date, 
                	b.username, 
                	d.balance,
                	充值总金额, 
                	支付总金额, 
                	退款总金额,
                	奖励总金额, 
                	其他总金额 
					FROM 
						(SELECT 
							DATE_FORMAT(create_time, '%Y-%m-%d') time, 
							username, 
							sum(CASE WHEN channel = 0 THEN amt ELSE 0 END ) AS 充值总金额,
							sum(CASE WHEN channel = 1 THEN amt ELSE 0 END ) AS 支付总金额,
							sum(CASE WHEN channel = 2 THEN amt ELSE 0 END ) AS 退款总金额,
							sum(CASE WHEN channel = 3 THEN amt ELSE 0 END ) AS 奖励总金额,
							sum(CASE WHEN channel = 4 THEN amt ELSE 0 END ) AS 其他总金额
						FROM 
							bills 
						WHERE 
							status = 2 AND create_time < v_endDate AND create_time >= v_beginDate 
								GROUP BY 
									time, username) b,datebalance d 
					WHERE
						b.username = d.username AND b.time = d.date;
                    

                SELECT ROW_COUNT() INTO o_result;
                RETURN o_result;
            END
$$
DELIMITER ;


DROP FUNCTION IF EXISTS `stat_orders`;
DELIMITER $$
CREATE FUNCTION `stat_orders`(v_beginDate varchar(10), v_endDate varchar(10)) RETURNS int(11)
BEGIN
                DECLARE o_result int;
                set o_result = -1;

                /* 避免重复统计，先清除信息 */
                DELETE FROM order_stats WHERE stat_date >= v_beginDate and stat_date < v_endDate;

                INSERT INTO order_stats (
                    stat_date,
                    username,
                    product_id,
                    operator,
                    sup_id,
                    province,
                    total_count,
                    wait_recharge_sum,
                    recharging_sum,
                    recharge_ok_sum,
                    recharge_fail_sum,
                    total_price,
                    wait_recharge_price_sum,
                    recharging_price_sum,
                    recharge_ok_price_sum,
                    recharge_fail_price_sum
                ) SELECT
                    DATE_FORMAT(create_time, '%Y-%m-%d') AS 日期,
                    username,
                    product_id,
                    province,
                    sum(CASE WHEN (pay_state >= 1 and pay_state != 3) THEN 1 ELSE 0 END ) AS 订购总单数,
                    sum(CASE WHEN recharge_state = 0 THEN 1 ELSE 0 END ) AS 待充值_单数,
                    sum(CASE WHEN recharge_state = 1 THEN 1 ELSE 0 END ) AS 充值中_单数,
                    sum(CASE WHEN recharge_state = 2 THEN 1 ELSE 0 END ) AS 成功_单数,
                    sum(CASE WHEN recharge_state = 3 THEN 1 ELSE 0 END ) AS 失败_单数,
                    sum(CASE WHEN (pay_state >= 1 and pay_state != 3) THEN price ELSE 0 END ) AS 订购总金额,
                    sum(CASE WHEN recharge_state = 0 THEN price ELSE 0 END ) AS 待充值_金额,
                    sum(CASE WHEN recharge_state = 1 THEN price ELSE 0 END ) AS 充值中_金额,
                    sum(CASE WHEN recharge_state = 2 THEN price ELSE 0 END ) AS 成功_金额,
                    sum(CASE WHEN recharge_state = 3 THEN price ELSE 0 END ) AS 失败_金额
                FROM
                    orders
                WHERE
                	create_time < v_endDate 
                  	AND create_time >= v_beginDate
                GROUP BY
                    日期, username, product_id, province;

                SELECT ROW_COUNT() INTO o_result;
                RETURN o_result;
            END
$$
DELIMITER ;