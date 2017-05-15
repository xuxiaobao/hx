/**
 * Created by angus
 */
DiscountListPanel = function (config) {
    var serviceUrl = Desktop.contextPath + "/api/bill/search";
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
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '会员名：'
            },
            {
                x: 70,
                y: 10 + 30,
                width: 80,
                fieldLabel: '会员名',
                name: 'username',
                enableKeyEvents: true,
                readOnly: !IS_ADMIN,
                cls: IS_ADMIN ? '' : 'x-item-disabled',
                scope: this,
                listeners: {
                    scope: this,
                    keydown: function (field, event) {
                        if (event.keyCode == 13) { //回车键事件
                            me.searchBtnClick();
                        }
                    }
                },
                value: (IS_ADMIN ? "" : CURR_USER)
            },
            {
                x: 180,
                y: 12 + 30,
                width: 80,
                xtype: 'label',
                text: '供货商编号：'
            },
            {
                x: 240,
                y: 10 + 30,
                width: 130,
                fieldLabel: '供货商编号',
                name: 'supId',
                enableKeyEvents: true,
                scope: this,
                listeners: {
                    scope: this,
                    keydown: function (field, event) {
                        if (event.keyCode == 13) { //回车键事件
                            me.searchBtnClick();
                        }
                    }
                }
            },
            {
                x: 660,
                y: 10,
                width: 70,
                xtype: 'button',
                id: 'BillListSearchBtn',
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
        fields: ['id', 'username', 'amt', 'oldBalance', 'type', 'channel', 'info', 'status', 'createTime'],
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

    var tbar = [];
    if(IS_ADMIN){
        tbar = [{
            text: '入 账',
            iconCls: 'icon-bill-checkin',
            scope: this,
            ref: '../checkinBtn',
            disabled: true,
            handler: this.checkinBtnHandler
        }];
    }

    // 表格面板
    var gridPanel = new Ext.grid.GridPanel({
        region: 'center',
        stripeRows: true,
        loadMask: {
            msg: "努力加载数据中，请稍后..."
        },
        store: store,
        sm: new Ext.grid.RowSelectionModel({
            //singleSelect: true,
            listeners: {
                scope: this,
                selectionchange: function (sm) {
                    var record = sm.getSelected();
                    gridPanel.checkinBtn.setDisabled(sm.getCount() != 1 || record.get("status") != 3);
                }
            }
        }),
        tbar: tbar,
        columns: [
            new Ext.grid.RowNumberer({width: 25}),
            {id: 'id', align: 'center', header: "账单编号", width: 150, sortable: true, dataIndex: 'id'},
            {id: 'username', align: 'center', header: "用户名", width: 120, sortable: true, dataIndex: 'username'},
            {id: 'type', align: 'center', header: "流水类型", width: 90, sortable: true, dataIndex: 'type',
                renderer: function (text) {
                    if(text == 0 || text == 'ADD'){
                        return '<span style="color: green">加钱</span>';
                    }
                    if(text == 1 || text=='SUBTRACTION'){
                        return '<span style="color: sienna">减钱</span>';
                    }
                }
            },
            {id: 'channel', align: 'center', header: "流水来源", width: 90, sortable: true, dataIndex: 'channel',
                renderer: function (text) {
                    switch (text){

                        case 'RECHARGE': return '充值';
                        case 'PAYMENT': return '支付';
                        case 'REFUND': return '退款';
                        case 'REWARD': return '奖励';
                        case 'OTHERS': return '其它';


                        case 0: return '充值';
                        case 1: return '支付';
                        case 2: return '退款';
                        case 3: return '奖励';
                        case 4: return '其它';
                        default: return text;
                    }
                }
            },
            {id: 'amt', align: 'center', header: "金额", width: 120, sortable: true, dataIndex: 'amt',
                renderer: function(text) {
                    return text >= 0 ? '<span style="color: green">' + text + '</span>': '<span style="color: #ffb941">' + text +'</span>' ;
                }
            },
            {id: 'status', align: 'center', header: "流水状态", width: 90, sortable: true, dataIndex: 'status',
                renderer: function (text) {
                    switch (text){
                        case 'INIT': return '待处理';
                        case 'PROCESS': return '<span style="color: #ffb941">处理中</span>';
                        case 'SUCCESS': return '<span style="color: green">成功</span>';
                        case 'FAILED': return '<span style="color: red">失败</span>';


                        case 0: return '待处理';
                        case 1: return '<span style="color: #ffb941">处理中</span>';
                        case 2: return '<span style="color: green">成功</span>';
                        case 3: return '<span style="color: red">失败</span>';
                        default: return text;
                    }
                }
            },
            {id: 'info', align: 'center', header: "备注信息", width: 180, sortable: true, dataIndex: 'info'},
            {id: 'createTime', align: 'center', header: "创建时间", width: 150, sortable: true, dataIndex: 'createTime'}
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
    DiscountListPanel.superclass.constructor.call(this, allConfig);
};

Ext.extend(DiscountListPanel, Ext.Panel, {
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

            var btn = Ext.getCmp("BillListSearchBtn");
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
                // do noting
                next();
            }
        });
    },

    /**
     * 入账按钮事件
     */
    checkinBtnHandler: function () {
        var s = this.gridPanel.getSelectionModel().getSelections();

        if(s.length == 0){
            return;
        }

        var me = this;

        Ext.Msg.show({
            title: '确认',
            msg: '请确认是否重新入账['+s[0].get("id")+']?',
            scope: this,
            buttons: Ext.Msg.YESNO,
            icon: Ext.MessageBox.QUESTION,
            fn: function (buttonId, text, opt) {
                if (buttonId === 'yes') {
                    Ext.Ajax.request({
                        url: Desktop.contextPath + "/api/bill/checkin",
                        method: "POST",
                        params: {
                            id: s[0].get("id")
                        },
                        callback: function (option, success, resp) {
                            var result = Ext.decode(resp.responseText);
                            if (success && resp.status === 200 && result.success) {
                                me.store.load();
                                return;
                            }
                            console.warn("delete failed");
                            Ext.Msg.show({
                                title: '操作失败',
                                msg: result.message,
                                buttons: Ext.Msg.OK,
                                icon: Ext.MessageBox.ERROR
                            });
                        }
                    });
                }
            }
        });
    }
});

Ext.reg('tab.discount', DiscountListPanel);

Ext.apply(Ext.form.VTypes, {
    dateVtype: function(val, field){
        var date = Date.parseDate(val, "Y-m-d H:i:s");
        return date ? true : false;
    },
    dateVtypeText: '请使用"yyyy-MM-dd HH:mm:ss"的时间格式'
});

