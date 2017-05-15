/**
 * Created by angus
 */
ExportListPanel = function(config) {

	var serviceUrl = Desktop.contextPath + "/api/export/search";
	var me = this;

	var now = new Date();
	var beforeHalfHour = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000); // 半小时前

	// 搜索面板
	var formPanel = new Ext.form.FormPanel({
		autoScroll : true,
		containerScroll : true,
		bodyStyle : 'padding:5px 5px 0',
		region : 'center',
		labelWidth : 75,
		border : false,
		defaultType : 'textfield',
		layout : 'absolute',
		defaults : {
			// 应用于每个被包含的项 applied to each contained item
			width : 370,
			msgTarget : 'side'
		},
		layoutConfig : {
			// 这里是布局配置项 layout-specific configs go here
			labelSeparator : ':'
		},
		items : [
				{
					x : 10,
					y : 12,
					width : 60,
					xtype : 'label',
					text : '创建时间：'
				},
				{
					x : 70,
					y : 10,
					width : 110,
					id : 'export_begin',
					name : 'begin',
					vtype : 'dayVtype',
					value : Util.formatDate2(beforeHalfHour),
					cls : 'Wdate',
					listeners : {
						render : function(p) {
							p.getEl().on('click', function() {
								WdatePicker({
									el : 'export_begin',
									dateFmt : 'yyyy-MM-dd'
								});
							});
						}
					}
				},
				{
					x : 195,
					y : 12,
					width : 15,
					xtype : 'label',
					text : '-'
				},
				{
					x : 215,
					y : 10,
					width : 110,
					fieldLabel : '结束',
					id : 'export_end',
					name : 'end',
					vtype : 'dayVtype',
					value : Util.formatDate2(now),
					cls : 'Wdate',
					listeners : {
						render : function(p) {
							p.getEl().on('click', function() {
								WdatePicker({
									el : 'export_end',
									dateFmt : 'yyyy-MM-dd'
								});
							});
						}
					}
				},
				{
					x : 10,
					y : 12 + 30,
					width : 60,
					xtype : 'label',
					text : '会员名：'
				},
				{
					x : 70,
					y : 10 + 30,
					width : 90,
					xtype : 'combo',
					typeAhead : true,
					triggerAction : 'all',
					lazyRender : true,
					mode : 'local',
					hiddenName : 'username',
					store: new Ext.data.Store({
	                	reader:new Ext.data.JsonReader({
	                		fields:['id','name'],
	                		root:'data'
	                	}),
	                	proxy:new Ext.data.HttpProxy({
	                		url: Desktop.contextPath + "/api/system/user/queryuserlist"
	                	}),
	                	autoLoad:true
	                }),
					forceSelection : true,
					//editable :false,
					emptyText : '请选择',
					valueField : 'id',
					displayField : 'name'
				},
				/*{
				    x: 170,
				    y: 12 + 30,
				    width: 60,
				    xtype: 'label',
				    text: '运营商：'
				},
				{
				    x: 230,
				    y: 10 + 30,
				    width: 70,
				    xtype: 'combo',
				    typeAhead: true,
				    triggerAction: 'all',
				    lazyRender: true,
				    mode: 'local',
				    hiddenName: 'operator',
				    store: new Ext.data.ArrayStore({
				        id: 0,
				        fields: [
				            'id',
				            'text'
				        ],
				        data: [['移动', '移动'], ['联通', '联通'], ['电信', '电信']]
				    }),
				    forceSelection: true,
				    //editable :false,
				    emptyText: '请选择',
				    valueField: 'id',
				    displayField: 'text'
				},
				{
				    x: 310,
				    y: 12 + 30,
				    width: 60,
				    xtype: 'label',
				    text: IS_ADMIN ? '供货商：' : ''
				},
				{
				    x: 370,
				    y: 10 + 30,
				    width: 80,
				    xtype: 'combo',
				    typeAhead: true,
				    triggerAction: 'all',
				    lazyRender: true,
				    mode: 'local',
				    hiddenName: 'supId',
				    store: new Ext.data.ArrayStore({
				        id: 0,
				        fields: [
				            'id',
				            'text'
				        ],
				        data: [['1', '华众'], ['2', '比特峰'], ['3', '好亚飞达'], ['4', '卓望'], ['5', '易途客']]
				    }),
				    forceSelection: true,
				    //editable :false,
				    emptyText: '请选择',
				    valueField: 'id',
				    displayField: 'text',
				    hidden: IS_ADMIN ? false : true,
				    hideLabel: IS_ADMIN ? false : true
				},*/
				{
					x : 600,
					y : 10,
					width : 70,
					xtype : 'button',
					id : 'ExportListSearchBtn',
					text : '搜  索',
					scope : this,
					handler : this.searchBtnClick
				}, {
					x : 600,
					y : 10 + 30,
					width : 70,
					xtype : 'button',
					id : 'ExportBtn',
					text : '导  出',
					scope : this,
					handler : this.exportBtnClick
				} ]
	});

	var proxy = new Ext.data.HttpProxy({
		url : serviceUrl,
		timeout : 10000,
		method : 'GET'
	});

	var store = new Ext.data.JsonStore({
		autoLoad : true,
		bodyStyle : 'padding:5px 5px 0',
		root : 'data',
		totalProperty : 'totalCount',
		proxy : proxy,
		remoteSort : true,
		fields : [ 'id', 'username', 'exportBegin', 'exportEnd', 'createTime',
				'exportTime', 'resource' ],
		listeners : {
			scope : this,
			"beforeload" : function(store) {
				var params = me.formPanel.getForm().getFieldValues();
				params.limit = Desktop.pageSize;
				store.baseParams = params;
			}
		}
	});

	var pageBarDisplayMsg = '当前展示第{0} - {1}条；共{2}条；';

	// 分页条
	var pagingbar = new Ext.PagingToolbar({
		pageSize : Desktop.pageSize,
		store : store,
		displayInfo : true,
		displayMsg : pageBarDisplayMsg,
		emptyMsg : '未搜索到记录'
	//plugins: new Ext.ux.ProgressBarPager()
	});
	pagingbar.inputItem.setDisabled(true);

	var gridColumns = [];
	gridColumns.push(new Ext.grid.RowNumberer({
		width : 25
	}));
	gridColumns.push({
		id : 'id',
		align : 'center',
		header : "编号",
		width : 120,
		sortable : true,
		dataIndex : 'id'
	});
	gridColumns.push({
		id : 'username',
		align : 'center',
		header : "用户名",
		width : 90,
		sortable : true,
		dataIndex : 'username'
	});
	gridColumns.push({
		id : 'exportBegin',
		align : 'center',
		header : "起始日期",
		width : 100,
		sortable : true,
		dataIndex : 'exportBegin'
	});
	gridColumns.push({
		id : 'exportEnd',
		align : 'center',
		header : "截至日期",
		width : 100,
		sortable : true,
		dataIndex : 'exportEnd'
	});
	gridColumns.push({
		id : 'createTime',
		align : 'center',
		header : "创建时间",
		width : 140,
		sortable : true,
		dataIndex : 'createTime'
	});
	gridColumns.push({
		id : 'exportTime',
		align : 'center',
		header : "导出时间",
		width : 140,
		sortable : true,
		dataIndex : 'exportTime'
	});
	gridColumns.push({
		id : 'resource',
		align : 'center',
		header : "下载",
		width : 100,
		sortable : true,
		dataIndex : 'resource',
		renderer : function(text) {
			return "<a href='" + text + "'>点击下载</a>";
		}
	});
	// 表格面板
	var gridPanel = new Ext.grid.GridPanel({
		region : 'center',
		stripeRows : true,
		loadMask : {
			msg : "努力加载数据中，请稍后..."
		},
		store : store,
		sm : new Ext.grid.RowSelectionModel({
			//singleSelect: true,
			listeners : {
				scope : this,
				selectionchange : function(sm) {
				}
			}
		}),
		columns : gridColumns,
		bbar : pagingbar,
		listeners : {
			rowclick : function(grid, rowIndex, e) {

			}
		}
	});

	var cfg = {
		closable : true,
		autoScroll : true,
		layout : 'border',
		margins : '35 5 5 0',
		containerScroll : true,
		items : [ {
			height : 68,
			maxHeight : 80,
			border : false,
			split : true,
			collapseMode : 'mini',
			region : 'north',
			layout : 'border',
			items : [ formPanel ]
		}, gridPanel ]
	};

	// 设置为成员属性
	this.formPanel = formPanel;
	this.gridPanel = gridPanel;
	this.store = store;

	var allConfig = Ext.applyIf(config || {}, cfg);
	ExportListPanel.superclass.constructor.call(this, allConfig);
};

Ext.extend(ExportListPanel, Ext.Panel, {
	/**
	 * 搜索按钮点击动作
	 */
	searchBtnClick : function() {
		var frm = this.formPanel.getForm();
		if (frm.isValid()) {
			var beginStr = frm.findField("begin").getValue();
			var endStr = frm.findField("end").getValue();

			var begin = Date.parseDate(beginStr, "Y-m-d");
			var end = Date.parseDate(endStr, "Y-m-d");

			//最大只支持跨度1天
			if (end.getTime() - begin.getTime() > 1000 * 60 * 60 * 24 * 31) {
				alert("时间范围最大只支持跨度30天");
				return;
			}

			var params = frm.getFieldValues();
			params.start = 0;
			params.limit = Desktop.pageSize;

			var btn = Ext.getCmp("ExportListSearchBtn");
			btn.disabled = true;
			this.loadGridData(params, function() {
				btn.disabled = false;
			})
		}
		;
	},
	/**
	 * 
	 */
	exportBtnClick : function() {
		var frm = this.formPanel.getForm();
		if (frm.isValid()) {
			var beginStr = frm.findField("begin").getValue();
			var endStr = frm.findField("end").getValue();

			var begin = Date.parseDate(beginStr, "Y-m-d");
			var end = Date.parseDate(endStr, "Y-m-d");

			//最大只支持跨度1天
			if (end.getTime() - begin.getTime() > 1000 * 60 * 60 * 24 * 31) {
				alert("时间范围最大只支持跨度30天");
				return;
			}

			var params = frm.getFieldValues();

			var btn = Ext.getCmp("ExportBtn");
			btn.disabled = true;

			Ext.Msg.confirm('消息', '确认提交导出请求吗?', function(optional) {
				if (optional == 'yes') {
					Ext.Ajax.request({
						url : Desktop.contextPath + "/api/export/doexport",
						method : "POST",
						params : params,
						success : function(response) {
							if (response) {
								var json = eval("(" + response.responseText
										+ ")");
								if (json.success) {
									alert('提交成功');
								} else {
									alert('提交失败');
								}
							}
						},
					});
				}
				btn.disabled = false;
			});
		}
		;
	},

	/**
	 * 加载表格数据
	 * @param params
	 * @param next
	 */
	loadGridData : function(params, next) {
		//var text = this.pagingBar.displayItem.autoEl.html || this.pagingBar.displayItem.el.dom.innerHTML;
		this.gridPanel.getStore().load({
			params : params,
			scope : this,
			callback : function(records, options, success) {
				if (!success) {
					window.location.reload(true);
				}
				// do noting
				next();
			}
		});
	}
});

Ext.reg('tab.exportlist', ExportListPanel);

Ext.apply(Ext.form.VTypes, {
	dayVtype : function(val, field) {
		var date = Date.parseDate(val, "Y-m-d");
		return date ? true : false;
	},
	dateVtypeText : '请使用"yyyy-MM-dd"的时间格式'
});
