/**
 * Created by angus
 */

var dateCondi = true;
var userCondi = true;
var provinceCondi = true;
var productCondi = true;
var operatorCondi = true;
var supCondi = true;
OrderStatListPanel = function (config) {

    var serviceUrl = Desktop.contextPath + "/api/orderstat/search";
    var me = this;

    var now = new Date();
    var yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000); // 昨天

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
                text: '统计日期：'
            },
            {
                x: 70,
                y: 10,
                width: 140,
                id: 'orderStatList_begin',
                name: 'begin',
                vtype: 'date2Vtype',
                value: Util.formatDate2(yesterday),
                cls: 'Wdate',
                listeners: {
                    render:function(p){
                        p.getEl().on('click',function(){
                            WdatePicker({el:'orderStatList_begin', dateFmt: 'yyyy-MM-dd'});
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
                id: 'orderStatList_end',
                name: 'end',
                vtype: 'date2Vtype',
                value: Util.formatDate2(yesterday),
                cls: 'Wdate',
                listeners: {
                    render:function(p){
                        p.getEl().on('click',function(){
                            WdatePicker({el:'orderStatList_end', dateFmt: 'yyyy-MM-dd'});
                        });
                    }
                }
            },
            {
                x: 380,
                y: 12,
                width: 60,
                xtype: 'label',
                text: '会员名：'
            },
            {
                x: 440,
                y: 10,
                width: 130,
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
                x: 580,
                y: 12,
                width: 60,
                xtype: 'label',
                text: '运营商：'
            },
            {
                x: 630,
                y: 10,
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
                x: 710,
                y: 12,
                width: 60,
                xtype: 'label',
                text: IS_ADMIN ? '供货商：' : ''
            },
            {
                x: 760,
                y: 10,
                width: 90,
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
                x: 10,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '省份：'
            },
            {
                x: 70,
                y: 10 + 30,
                width: 130,
                fieldLabel: '省份',
                name: 'province',
                enableKeyEvents: true,
                scope: this,
                listeners: {
                    scope: this,
                    keydown: function (field, event) {
                        if (event.keyCode == 13) { //回车键事件
                            me.searchBtnClick();
                        }
                    }
                },
                value: ''
            },
            {
                x: 210,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '订购商品：'
            },
            {
                x: 270,
                y: 10 + 30,
                width: 170,
                xtype: 'combo',
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                mode: 'local',
                hiddenName: 'productId',
                store: new Ext.data.Store({
                	reader:new Ext.data.JsonReader({
                		fields:['id','name'],
                		root:'data'
                	}),
                	proxy:new Ext.data.HttpProxy({
                		url: Desktop.contextPath + "/api/product/search?start=0&limit=100"
                	}),
                	autoLoad:true
                }),
                forceSelection: true,
                //editable :false,
                emptyText: '请选择充值产品',
                valueField: 'id',
                displayField: 'name'
            },
            {
                x: 450,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '分组条件：'
            },
            {
                x: 510,
                y: 10 + 30,
                width: 320,
                xtype: 'checkboxgroup',
                fieldLabel: '分组条件',
                //name: 'groupConditions',
                items: [
                    {boxLabel: '日期', name: 'groupConditions', inputValue: 0, checked: true},
                    {boxLabel: '用户', name: 'groupConditions', inputValue: 1, checked: true},
                    {boxLabel: '省份', name: 'groupConditions', inputValue: 2, checked: true},
                    {boxLabel: '商品', name: 'groupConditions', inputValue: 3, checked: true},
                    {boxLabel: '运营商', name: 'groupConditions', inputValue: 4, checked: true},
                    {boxLabel: '供应商', name: 'groupConditions', inputValue: 5, checked: true}
                ]
            },
            {
                x: 870,
                y: 10,
                width: 70,
                xtype: 'button',
                id: 'OrderStatListSearchBtn',
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
        fields: ['id', 'statDate', 'username', 'productId', 'operator', 'supId', 'province', 'totalCount', 'waitRechargeSum', 'rechargingSum', 'rechargeOkSum', 'rechargeFailSum',
            'totalPrice', 'waitRechargePriceSum', 'rechargingPriceSum', 'rechargeOkPriceSum', 'rechargeFailPriceSum', 'rechargeOkRate'],
        listeners: {
            scope: this,
            "beforeload": function (store) {
                var params = me.formPanel.getForm().getValues();
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
        plugins: new Ext.ux.grid.ColumnHeaderGroup({
            rows: [
                [
                    {header: '编号', colspan: 1, align: 'center'},
                    {header: '统计日期', colspan: 1, align: 'center'},
                    {header: '会员名', colspan: 1, align: 'center'},
                    {header: '商品代码', colspan: 1, align: 'center'},
                    {header: '运营商', colspan: 1, align: 'center'},
                    {header: '供货商', colspan: 1, align: 'center'},
                    {header: '省份', colspan: 1, align: 'center'},
                    {header: '订单数量', colspan: 5, align: 'center'},
                    {header: '订单金额', colspan: 5, align: 'center'},
                    {header: '订单成功率', colspan: 1, align: 'center'}
                ]
            ]
        }),
        columns: [
            {id: 'id', align: 'center', header: "-", width: 40, sortable: true, dataIndex: 'id'},
            {id: 'statDate', align: 'center', header: "-", width: 100, sortable: true, dataIndex: 'statDate',
                renderer: function (text) {
                    if(!dateCondi){
                        return "ALL";
                    }
                    return text;
                }},
            {id: 'username', align: 'center', header: "-", width: 80, sortable: true, dataIndex: 'username',
                renderer: function (text) {
                if(!userCondi){
                    return "ALL";
                }
                    return text;
            }},
            {id: 'productId', align: 'center', header: "-", width: 80, sortable: true, dataIndex: 'productId',
                renderer: function (text) {
                if(!productCondi){
                    return "ALL";
                }
                    return text;
            }},
            {id: 'operator', align: 'center', header: "-", width: 80, sortable: true, dataIndex: 'operator',
                renderer: function (text) {
                if(!operatorCondi){
                    return "ALL";
                }
                    return text;
            }},
            {id: 'supId', align: 'center', header: "-", width: 80, sortable: true, dataIndex: 'supId',
                renderer: function (text) {
                if(!supCondi){
                    return "ALL";
                }
                    return text;
            }},
            {id: 'province', align: 'center', header: "-", width: 80, sortable: true, dataIndex: 'province',
                renderer: function (text) {
                if(!provinceCondi){
                    return "ALL";
                }
                    return text;
            }},
            {id: 'totalCount', align: 'center', header: "总数", width: 60, sortable: true, dataIndex: 'totalCount'},
            {id: 'waitRechargeSum', align: 'center', header: "待充值", width: 60, sortable: true, dataIndex: 'waitRechargeSum', renderer: function (text) {return '<span style="color: #ffb941">' + text + '</span>'}},
            {id: 'rechargingSum', align: 'center', header: "充值中", width: 60, sortable: true, dataIndex: 'rechargingSum', renderer: function (text) {return '<span style="color: aqua">' + text + '</span>'}},
            {id: 'rechargeOkSum', align: 'center', header: "成功", width: 60, sortable: true, dataIndex: 'rechargeOkSum', renderer: function (text) {return '<span style="color: green">' + text + '</span>'}},
            {id: 'rechargeFailSum', align: 'center', header: "失败", width: 60, sortable: true, dataIndex: 'rechargeFailSum', renderer: function (text) {return '<span style="color: red">' + text + '</span>'}},
            {id: 'totalPrice', align: 'center', header: "总金额", width: 60, sortable: true, dataIndex: 'totalPrice'},
            {id: 'waitRechargePriceSum', align: 'center', header: "待充值", width: 60, sortable: true, dataIndex: 'waitRechargePriceSum', renderer: function (text) {return '<span style="color: #ffb941">' + text + '</span>'}},
            {id: 'rechargingPriceSum', align: 'center', header: "充值中", width: 60, sortable: true, dataIndex: 'rechargingPriceSum', renderer: function (text) {return '<span style="color: aqua">' + text + '</span>'}},
            {id: 'rechargeOkPriceSum', align: 'center', header: "成功", width: 60, sortable: true, dataIndex: 'rechargeOkPriceSum', renderer: function (text) {return '<span style="color: green">' + text + '</span>'}},
            {id: 'rechargeFailPriceSum', align: 'center', header: "失败", width: 60, sortable: true, dataIndex: 'rechargeFailPriceSum', renderer: function (text) {return '<span style="color: red">' + text + '</span>'}},
            {id: 'rechargeOkRate', align: 'center', header: "-", width: 80, sortable: true, dataIndex: 'rechargeOkRate',
                renderer: function (text) {
                    if(parseFloat(text) >= 70){
                        return '<span style="color: green">' + text + '</span>'
                    }else  if(parseFloat(text) >= 50) {
                        return '<span style="color: #ffb941">' + text + '</span>'
                    }else{
                        return '<span style="color: red">' + text + '</span>'
                    }
                }
            }
        ],
        bbar: pagingbar
    });

    var statGrid = new Ext.grid.PropertyGrid({
        title: '数据汇总',
        split: true,
        collapseMode: 'mini',
        maxWidth: 300,
        minWidth: 200,
        region: 'east',
        width: 300,
        loadMask: {
            msg: "努力加载数据中，请稍后..."
        },
        //hideHeaders: true,
        //disabled: true,
        customRenderers: {
            b_waitRechargeSum: function (text) {return '<span style="color: #ffb941">' + text + '</span>'},
            c_rechargingSum: function (text) {return '<span style="color: aqua">' + text + '</span>'},
            d_rechargeOkSum: function (text) {return '<span style="color: green">' + text + '</span>'},
            e_rechargeFailSum: function (text) {return '<span style="color: red">' + text + '</span>'},
            g_waitRechargePriceSum: function (text) {return '<span style="color: #ffb941">' + text + '</span>'},
            h_rechargingPriceSum: function (text) {return '<span style="color: aqua">' + text + '</span>'},
            i_rechargeOkPriceSum: function (text) {return '<span style="color: green">' + text + '</span>'},
            j_rechargeFailPriceSum: function (text) {return '<span style="color: red">' + text + '</span>'},
            k_rechargeOkRate: function (text) {
                if(parseFloat(text) >= 70){
                    return '<span style="color: green">' + text + '</span>'
                }else  if(parseFloat(text) >= 50) {
                    return '<span style="color: #ffb941">' + text + '</span>'
                }else{
                    return '<span style="color: red">' + text + '</span>'
                }
            }
        },
        clicksToEdit: 2,
        propertyNames: {
            a_totalCount: '订购总单数',
            b_waitRechargeSum: '待充值总数',
            c_rechargingSum: '充值中总数',
            d_rechargeOkSum: '充值成功总数',
            e_rechargeFailSum: '充值失败总数',
            f_totalPrice: '订购总金额',
            g_waitRechargePriceSum: '待充值总金额',
            h_rechargingPriceSum: '充值中总金额',
            i_rechargeOkPriceSum: '充值成功总金额',
            j_rechargeFailPriceSum: '充值失败总金额',
            k_rechargeOkRate: '充值成功率'
        },
        viewConfig : {
            forceFit: true,
            scrollOffset: 2 // the grid will never have scrollbars
        }
    });

    Ext.Ajax.request({
        url: Desktop.contextPath + "/api/orderstat/sum",
        method: "POST",
        params: formPanel.getForm().getFieldValues(),
        callback: function (option, success, resp) {
            var result = Ext.decode(resp.responseText);
            if (success && resp.status === 200 && result.success) {
                statGrid.setSource(result.data);
                return;
            }
            console.warn("load order sum failed");
        }
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
            gridPanel,
            statGrid
        ]
    };

    // 设置为成员属性
    this.formPanel = formPanel;
    this.gridPanel = gridPanel;
    this.statGrid = statGrid;
    this.store = store;

    var allConfig = Ext.applyIf(config || {}, cfg);
    OrderStatListPanel.superclass.constructor.call(this, allConfig);
};

Ext.extend(OrderStatListPanel, Ext.Panel, {

    /**
     * 搜索按钮点击动作
     */
    searchBtnClick: function () {
        var frm = this.formPanel.getForm();
        if (frm.isValid()) {

            var conditions = document.getElementsByName("groupConditions");
            for(var i=0;i<conditions.length;i++){
                if(conditions[i].checked){
                    if(conditions[i].value==0){
                        dateCondi = true;
                    }

                    if(conditions[i].value==1){
                        userCondi = true;
                    }

                    if(conditions[i].value==2){
                        provinceCondi = true;
                    }

                    if(conditions[i].value==3){
                        productCondi = true;
                    }
                    
                    if(conditions[i].value==4){
                    	operatorCondi = true;
                    }
                    
                    if(conditions[i].value==5){
                    	supCondi = true;
                    }
                    
                }else{
                    if(conditions[i].value==0){
                        dateCondi = false;
                    }

                    if(conditions[i].value==1){
                        userCondi = false;
                    }

                    if(conditions[i].value==2){
                        provinceCondi = false;
                    }

                    if(conditions[i].value==3){
                        productCondi = false;
                    }
                    
                    if(conditions[i].value==4){
                    	operatorCondi = false;
                    }
                    
                    if(conditions[i].value==5){
                    	supCondi = false;
                    }
                }
            }


            var beginStr = frm.findField("begin").getValue();
            var endStr = frm.findField("end").getValue();
            var begin = Date.parseDate(beginStr, "Y-m-d");
            var end = Date.parseDate(endStr, "Y-m-d");

            //最大只支持跨度1天
            if (end.getTime() - begin.getTime() > 1000 * 60 * 60 * 24 * 60) {
                alert("时间范围最大只支持跨度60天");
                return;
            }

            var params = frm.getValues();
            params.start = 0;
            params.limit = Desktop.pageSize;

            var btn = Ext.getCmp("OrderStatListSearchBtn");
            btn.disabled = true;
            this.loadGridData(params, function () {
                btn.disabled = false;
            });

            var statGrid = this.statGrid;
            Ext.Ajax.request({
                url: Desktop.contextPath + "/api/orderstat/sum",
                method: "POST",
                params: params,
                callback: function (option, success, resp) {
                    var result = Ext.decode(resp.responseText);
                    if (success && resp.status === 200 && result.success) {
                        statGrid.setSource(result.data);
                        return;
                    }
                    console.warn("load order sum failed");
                }
            });
        }
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

    showDetail: function(){

    }
});

Ext.reg('tab.orderstatlist', OrderStatListPanel);

Ext.apply(Ext.form.VTypes, {
    date2Vtype: function(val, field){
        var date = Date.parseDate(val, "Y-m-d");
        return date ? true : false;
    },
    date2VtypeText: '请使用"yyyy-MM-dd"的时间格式'
});

