/**
 * Created by angus
 */
ReasonStatListPanel = function (config) {
    var serviceUrl = Desktop.contextPath + "/api/orderstat/queryfailreason";
    var me = this;

    var now = new Date();
    var beforeHalfHour = new Date(now.getTime() - 30 * 60 * 1000); // 半小时前

    // 搜索面板
    var formPanel = new Ext.form.FormPanel({
        autoScroll: true,
        containerScroll: true,
        bodyStyle: 'padding:5px 5px 0',
        region: 'center',
        labelWidth: 75,
        border: false,
        defaultType: 'textfield',
        layout: 'absolute',
        defaults: {
            // 应用于每个被包含的项 applied to each contained item
            width: 370,
            msgTarget: 'side'
        },
        layoutConfig: {
            // 这里是布局配置项 layout-specific configs go here
            labelSeparator: ':'
        },
        items: [
            {
                x: 10,
                y: 12,
                width: 60,
                xtype: 'label',
                text: '创建时间：'
            },
            {
                x: 70,
                y: 10,
                width: 140,
                id: 'reasonStatList_begin',
                name: 'begin',
                vtype: 'dateVtype',
                value: Util.formatDate(beforeHalfHour),
                cls: 'Wdate',
                listeners: {
                    render:function(p){
                        p.getEl().on('click',function(){
                            WdatePicker({el:'reasonStatList_begin', dateFmt: 'yyyy-MM-dd HH:mm:ss'});
                        });
                    }
                }
            },
            {
                x: 215,
                y: 12,
                width: 15,
                xtype: 'label',
                text: '-'
            },
            {
                x: 230,
                y: 10,
                width: 140,
                fieldLabel: '结束',
                id: 'reasonStatList_end',
                name: 'end',
                vtype: 'dateVtype',
                value: Util.formatDate(now),
                cls: 'Wdate',
                listeners: {
                    render:function(p){
                        p.getEl().on('click',function(){
                            WdatePicker({el:'reasonStatList_end', dateFmt: 'yyyy-MM-dd HH:mm:ss'});
                        });
                    }
                }
            },
            {
                x: 390,
                y: 10,
                width: 70,
                xtype: 'button',
                text: '近半小时',
                scope: this,
                handler: function () {
                    this.quickSelectDateTime(30);
                }
            },
            {
                x: 460,
                y: 10,
                width: 70,
                xtype: 'button',
                text: '近两小时',
                scope: this,
                handler: function () {
                    this.quickSelectDateTime(120);
                }
            },
            {
                x: 530,
                y: 10,
                width: 70,
                xtype: 'button',
                text: '近一天',
                scope: this,
                handler: function () {
                    this.quickSelectDateTime(1440);
                }
            },
            {
                x: 10,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '会员名：'
            },
            {
                x: 70,
                y: 10 + 30,
                width: 90,
                xtype: 'combo',
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                mode: 'local',
                hiddenName: 'username',
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
                forceSelection: true,
                //editable :false,
                emptyText: '请选择',
                valueField: 'id',
                displayField: 'name'
            },
            {
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
                text: '供货商：'
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
                store: new Ext.data.Store({
                	reader:new Ext.data.JsonReader({
                		fields:['id','name'],
                		root:'data'
                	}),
                	proxy:new Ext.data.HttpProxy({
                		url: Desktop.contextPath + "/api/sp/querySupList"
                	}),
                	autoLoad:true
                }),
                forceSelection: true,
                //editable :false,
                emptyText: '请选择',
                valueField: 'id',
                displayField: 'name',
                hidden: IS_ADMIN ? false : true,
                hideLabel: IS_ADMIN ? false : true
            },
            {
                x: 660,
                y: 10,
                width: 70,
                xtype: 'button',
                id: 'reasonStatListSearchBtn',
                text: '搜  索',
                scope: this,
                handler: this.searchBtnClick
            }

        ]
    });

    var proxy = new Ext.data.HttpProxy({
        url: serviceUrl,
        timeout: 10000,
        method: 'GET'
    });

    var store = new Ext.data.JsonStore({
        autoLoad: true,
        bodyStyle: 'padding:5px 5px 0',
        root: 'data',
        totalProperty: 'totalCount',
        proxy: proxy,
        remoteSort: true,
        fields: ['reason', 'times'],
        listeners: {
            scope: this,
            "beforeload": function (store) {
                var params = me.formPanel.getForm().getFieldValues();
                params.limit = Desktop.pageSize;
                store.baseParams = params;
            }
        }
    });

    var pageBarDisplayMsg = '当前展示第{0} - {1}条；共{2}条；';

    // 分页条
    var pagingbar = new Ext.PagingToolbar({
        pageSize: Desktop.pageSize,
        store: store,
        displayInfo: true,
        displayMsg: pageBarDisplayMsg,
        emptyMsg: '未搜索到记录'
        //plugins: new Ext.ux.ProgressBarPager()
    });
    pagingbar.inputItem.setDisabled(true);


    // 表格面板
    var gridPanel = new Ext.grid.GridPanel({
        region: 'center',
        stripeRows: true,
        loadMask: {
            msg: "努力加载数据中，请稍后..."
        },
        store: store,
        columns: [
            new Ext.grid.RowNumberer({width: 25}),
            {id: 'reason', align: 'center', header: "失败原因", width: 750, sortable: true, dataIndex: 'reason'},
            {id: 'times', align: 'center', header: "失败次数", width: 120, sortable: true, dataIndex: 'times'}
        ],
        bbar: pagingbar
    });

    var cfg = {
        closable: true,
        autoScroll: true,
        layout: 'border',
        margins: '35 5 5 0',
        containerScroll: true,
        items: [{
            height: 80,
            border: false,
            //split: true,
            //collapseMode: 'mini',
            region: 'north',
            layout: 'border',
            items: [formPanel]
        },
            gridPanel
        ]
    };

    // 设置为成员属性
    this.formPanel = formPanel;
    this.gridPanel = gridPanel;
    this.store = store;

    var allConfig = Ext.applyIf(config || {}, cfg);
    ReasonStatListPanel.superclass.constructor.call(this, allConfig);
};

Ext.extend(ReasonStatListPanel, Ext.Panel, {
    /**
     * 快速选择时间按钮动作
     * @param beforeMins 前多少分钟
     */
    quickSelectDateTime: function (beforeMins) {
        var now = new Date();
        var frm = this.formPanel.getForm();

        frm.findField("begin").setValue(Util.formatDate(new Date(now.getTime() - beforeMins * 60 * 1000)));
        frm.findField("end").setValue(Util.formatDate(now));
    },

    /**
     * 搜索按钮点击动作
     */
    searchBtnClick: function () {
        var frm = this.formPanel.getForm();
        if (frm.isValid()) {
            var beginStr = frm.findField("begin").getValue();
            var endStr = frm.findField("end").getValue();

            var begin = Date.parseDate(beginStr, "Y-m-d H:i:s");
            var end = Date.parseDate(endStr, "Y-m-d H:i:s");

            //最大只支持跨度1天
            if (end.getTime() - begin.getTime() > 1000 * 60 * 60 * 24 * 7) {
                alert("时间范围最大只支持跨度7天");
                return;
            }

            var params = frm.getFieldValues();
            params.start = 0;
            params.limit = Desktop.pageSize;

            var btn = Ext.getCmp("reasonStatListSearchBtn");
            btn.disabled = true;
            this.loadGridData(params, function () {
                btn.disabled = false;
            })
        };
    },

    /**
     * 加载表格数据
     * @param params
     * @param next
     */
    loadGridData: function (params, next) {
        //var text = this.pagingBar.displayItem.autoEl.html || this.pagingBar.displayItem.el.dom.innerHTML;
        this.gridPanel.getStore().load({
            params: params,
            scope: this,
            callback: function (records, options, success) {
                if(!success){
                    window.location.reload(true);
                }
                next();
            }
        });
    },
});

Ext.reg('tab.reasonstatlist', ReasonStatListPanel);

Ext.apply(Ext.form.VTypes, {
    dateVtype: function(val, field){
        var date = Date.parseDate(val, "Y-m-d H:i:s");
        return date ? true : false;
    },
    dateVtypeText: '请使用"yyyy-MM-dd HH:mm:ss"的时间格式'
});

