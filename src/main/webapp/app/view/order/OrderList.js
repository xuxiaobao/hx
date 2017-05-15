/**
 * Created by angus
 */
OrderListPanel = function (config) {

    var serviceUrl = Desktop.contextPath + "/api/order/search";
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
                id: 'orderList_begin',
                name: 'begin',
                vtype: 'dateVtype',
                value: Util.formatDate(beforeHalfHour),
                cls: 'Wdate',
                listeners: {
                    render: function (p) {
                        p.getEl().on('click', function () {
                            WdatePicker({el: 'orderList_begin', dateFmt: 'yyyy-MM-dd HH:mm:ss'});
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
                id: 'orderList_end',
                name: 'end',
                vtype: 'dateVtype',
                value: Util.formatDate(now),
                cls: 'Wdate',
                listeners: {
                    render: function (p) {
                        p.getEl().on('click', function () {
                            WdatePicker({el: 'orderList_end', dateFmt: 'yyyy-MM-dd HH:mm:ss'});
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
                x: 600,
                y: 10,
                width: 70,
                xtype: 'button',
                text: '近七天',
                scope: this,
                handler: function () {
                    this.quickSelectDateTime(1440 * 7);
                }
            },
            {
                x: 10,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '会员名：'
            },
            IS_ADMIN ? {
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
            }:{
                x: 70,
                y: 10 + 30,
                width: 90,
                fieldLabel: '会员名',
                name: 'username',
                readOnly: !IS_ADMIN,
                cls: IS_ADMIN ? '' : 'x-item-disabled',
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
                value: (IS_ADMIN ? "" : CURR_USER)
            },
            {
                x: 170,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '定单编号：'
            },
            {
                x: 230,
                y: 10 + 30,
                width: 130,
                fieldLabel: '定单编号',
                name: 'id',
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
                x: 370,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '外部单号：'
            },
            {
                x: 430,
                y: 10 + 30,
                width: 130,
                fieldLabel: '外部单号',
                name: 'externalId',
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
                x: 570,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '充值号码：'
            },
            {
                x: 630,
                y: 10 + 30,
                width: 130,
                fieldLabel: '充值号码',
                name: 'phone',
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
                value: '',
                xtype: 'numberfield',
                allowDecimals: false,
                minLength: 8,
                maxLength: 18,
                allowNegative: false
            },
            {
                x: 770,
                y: 12 + 30,
                width: 60,
                xtype: 'label',
                text: '运营商：'
            },
            {
                x: 820,
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
                x: 10,
                y: 12 + 30 * 2,
                width: 60,
                xtype: 'label',
                text: '到账类型：'
            },
            {
                x: 70,
                y: 10 + 30 * 2,
                width: 60,
                xtype: 'combo',
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                mode: 'local',
                hiddenName: 'effectType',
                store: new Ext.data.ArrayStore({
                    id: 0,
                    fields: [
                        'id',
                        'text'
                    ],
                    data: [[0, '立即到账'], [1, '下月生效']]
                }),
                forceSelection: true,
                //editable :false,
                emptyText: '请选择',
                valueField: 'id',
                displayField: 'text'
            },
            {
                x: 140,
                y: 12 + 30 * 2,
                width: 60,
                xtype: 'label',
                text: '支付状态：'
            },
            {
                x: 200,
                y: 10 + 30 * 2,
                width: 60,
                xtype: 'combo',
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                mode: 'local',
                hiddenName: 'payState',
                store: new Ext.data.ArrayStore({
                    id: 0,
                    fields: [
                        'id',
                        'text'
                    ],
                    data: [[0, '待支付'], [1, '支付中'], [2, '已支付'], [3, '支付失败'], [4, '退款中'], [5, '已退款']]
                }),
                forceSelection: true,
                //editable :false,
                emptyText: '请选择',
                valueField: 'id',
                displayField: 'text'
            },
            {
                x: 270,
                y: 12 + 30 * 2,
                width: 60,
                xtype: 'label',
                text: '充值状态：'
            },
            {
                x: 330,
                y: 10 + 30 * 2,
                width: 60,
                xtype: 'combo',
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                mode: 'local',
                hiddenName: 'rechargeState',
                store: new Ext.data.ArrayStore({
                    id: 0,
                    fields: [
                        'id',
                        'text'
                    ],
                    data: [[0, '待充值'], [1, '充值中'], [2, '充值成功'], [3, '充值失败']]
                }),
                forceSelection: true,
                //editable :false,
                emptyText: '请选择',
                valueField: 'id',
                displayField: 'text'
            },
            {
                x: 400,
                y: 12 + 30 * 2,
                width: 60,
                xtype: 'label',
                text: '订购商品：'
            },
            {
                x: 450,
                y: 10 + 30 * 2,
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
                		url: Desktop.contextPath + "/api/product/search?start=0&limit=200"
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
                x: 650,
                y: 12 + 30 * 2,
                width: 60,
                xtype: 'label',
                text: IS_ADMIN ? '供货商：' : ''
            },
            {
                x: 700,
                y: 10 + 30 * 2,
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
                x: 690,
                y: 10,
                width: 70,
                xtype: 'button',
                id: 'OrderListSearchBtn',
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
        fields: ['id', 'username', 'phone', 'effectType', 'province', 'operator', 'externalId', 'productId', 'productName', 'productPrice', 'price', 'payState',
            'payFailedReason', 'payId', 'refundId', 'rechargeId', 'rechargeSystem', 'rechargeState', 'rechargeFailedReason', 'notifyUrl', 'payTime',
            'rechargeTime', 'createTime', 'rechargeEndTime','supId'],
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
    if (IS_ADMIN) {
        tbar = [{
            text: '充值核实',
            iconCls: 'icon-bill-checkin',
            scope: this,
            ref: '../checkinRechargeStatusBtn',
            disabled: true,
            handler: this.checkinRechargeStatus
        }, {
            text: '确认失败',
            iconCls: 'icon-confirm-failed',
            scope: this,
            ref: '../setToRechargeFailedBtn',
            disabled: true,
            handler: this.setToRechargeFailed
        }];
    }

    var gridColumns = [];

    gridColumns.push(new Ext.grid.RowNumberer({width: 25}));
    gridColumns.push({id: 'id', align: 'center', header: "订单编号", width: 120, sortable: true, dataIndex: 'id'});
    gridColumns.push({id: 'username', align: 'center', header: "用户名", width: 90, sortable: true, dataIndex: 'username'});
    gridColumns.push({id: 'phone', align: 'center', header: "号码", width: 90, sortable: true, dataIndex: 'phone'});
    gridColumns.push({id: 'price', align: 'center', header: "订单金额", width: 70, sortable: true, dataIndex: 'price'});
    gridColumns.push({
        id: 'effectType', align: 'center', header: "到账类型", width: 70, sortable: true, dataIndex: 'effectType', hidden: true,
        renderer: function (text) {
            switch (text) {
                case 0:
                    return '立即';
                case 1:
                    return '下月';
                default:
                    return text;
            }
        }
    });
    gridColumns.push({id: 'province', align: 'center', header: "省份", width: 70, sortable: true, dataIndex: 'province'});
    gridColumns.push({id: 'operator', align: 'center', header: "运营商", width: 60, sortable: true, dataIndex: 'operator'});
    gridColumns.push({id: 'externalId', align: 'center', header: "外部单号", width: 120, sortable: true, dataIndex: 'externalId'});
    gridColumns.push({id: 'productId', align: 'center', header: "商品代码", width: 90, sortable: true, dataIndex: 'productId'});
    gridColumns.push({id: 'productName', align: 'center', header: "商品名称", width: 120, sortable: true, dataIndex: 'productName', hidden: true});
    gridColumns.push({
        id: 'payState', align: 'center', header: "支付状态", width: 70, sortable: true, dataIndex: 'payState',
        renderer: function (text) {
            switch (text) {
                case 'INIT':
                    return '<span style="color: #ffb941">' + '待支付' + '</span>';
                case 'PROCESS':
                    return '<span style="color: aqua">' + '支付中' + '</span>';
                case 'SUCCESS':
                    return '<span style="color: green">' + '已支付' + '</span>';
                case 'FAILED':
                    return '<span style="color: red">' + '支付失败' + '</span>';
                case 'REFUND_PROCESS':
                    return '<span style="color: blue">' + '退款中' + '</span>';
                case 'REFUNDED':
                    return '<span style="color: purple">' + '已退款' + '</span>';

                case '0':
                    return '<span style="color: #ffb941">' + '待支付' + '</span>';
                case '1':
                    return '<span style="color: aqua">' + '支付中' + '</span>';
                case '2':
                    return '<span style="color: green">' + '已支付' + '</span>';
                case '3':
                    return '<span style="color: red">' + '支付失败' + '</span>';
                case '4':
                    return '<span style="color: blue">' + '退款中' + '</span>';
                case '5':
                    return '<span style="color: purple">' + '已退款' + '</span>';
                default:
                    return text;
            }
        }
    });
    gridColumns.push({id: 'payId', align: 'center', header: "支付单号", width: 100, sortable: true, dataIndex: 'payId', hidden: true});
    gridColumns.push({id: 'payFailedReason', align: 'center', header: "支付失败原因", width: 120, sortable: true, dataIndex: 'payFailedReason', hidden: true});
    gridColumns.push({id: 'refundId', align: 'center', header: "退款单号", width: 100, sortable: true, dataIndex: 'refundId', hidden: true});
    if(IS_ADMIN){
        gridColumns.push({id: 'rechargeId', align: 'center', header: "充值单号", width: 100, sortable: true, dataIndex: 'rechargeId', hidden: true});
    }
    gridColumns.push({
        id: 'rechargeState', align: 'center', header: "充值状态", width: 70, sortable: true, dataIndex: 'rechargeState',
        renderer: function (text) {
            switch (text) {
                case 'INIT':
                    return '<span style="color: #ffb941">' + '待充值' + '</span>';
                case 'PROCESS':
                    return '<span style="color: aqua">' + '充值中' + '</span>';
                case 'SUCCESS':
                    return '<span style="color: green">' + '充值成功' + '</span>';
                case 'FAILED':
                    return '<span style="color: red">' + '充值失败' + '</span>';

                case '0':
                    return '<span style="color: #ffb941">' + '待充值' + '</span>';
                case '1':
                    return '<span style="color: aqua">' + '充值中' + '</span>';
                case '2':
                    return '<span style="color: green">' + '充值成功' + '</span>';
                case '3':
                    return '<span style="color: red">' + '充值失败' + '</span>';
                default:
                    return text;
            }
        }
    });
    gridColumns.push({id: 'rechargeFailedReason', align: 'center', header: "充值失败原因", width: 140, sortable: true, dataIndex: 'rechargeFailedReason'});
    gridColumns.push({id: 'notifyUrl', align: 'center', header: "回调地址", width: 140, sortable: true, dataIndex: 'notifyUrl', hidden: true});
    gridColumns.push({id: 'createTime', align: 'center', header: "创建时间", width: 140, sortable: true, dataIndex: 'createTime'});
    if(IS_ADMIN){
        gridColumns.push({id: 'supId', align: 'center', header: "充值供货商", width: 140, sortable: true, dataIndex: 'supId'});
    }
    gridColumns.push({id: 'payTime', align: 'center', header: "支付时间", width: 90, sortable: true, dataIndex: 'payTime', hidden: true});
    gridColumns.push({id: 'rechargeTime', align: 'center', header: "充值时间", width: 140, sortable: true, dataIndex: 'rechargeTime'});
    gridColumns.push({id: 'rechargeEndTime', align: 'center', header: "充值结束时间", width: 140, sortable: true, dataIndex: 'rechargeEndTime'});

    var detailGrid = new Ext.grid.PropertyGrid({
        title: '详细信息',
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
            e_effectType: function (text) {
                switch (text) {
                    case 0:
                        return '立即';
                    case 1:
                        return '下月';
                    default:
                        return text;
                }
            },
            j_payState: function (text) {
                switch (text) {
                    case 'INIT':
                        return '<span style="color: #ffb941">' + '待支付' + '</span>';
                    case 'PROCESS':
                        return '<span style="color: aqua">' + '支付中' + '</span>';
                    case 'SUCCESS':
                        return '<span style="color: green">' + '已支付' + '</span>';
                    case 'FAILED':
                        return '<span style="color: red">' + '支付失败' + '</span>';
                    case 'REFUND_PROCESS':
                        return '<span style="color: blue">' + '退款中' + '</span>';
                    case 'REFUNDED':
                        return '<span style="color: purple">' + '已退款' + '</span>';


                    case '0':
                        return '<span style="color: #ffb941">' + '待支付' + '</span>';
                    case '1':
                        return '<span style="color: aqua">' + '支付中' + '</span>';
                    case '2':
                        return '<span style="color: green">' + '已支付' + '</span>';
                    case '3':
                        return '<span style="color: red">' + '支付失败' + '</span>';
                    case '4':
                        return '<span style="color: blue">' + '退款中' + '</span>';
                    case '5':
                        return '<span style="color: purple">' + '已退款' + '</span>';
                    default:
                        return text;
                }
            },
            o_rechargeState: function (text) {
                switch (text) {
                    case '0':
                        return '<span style="color: #ffb941">' + '待充值' + '</span>';
                    case '1':
                        return '<span style="color: aqua">' + '充值中' + '</span>';
                    case '2':
                        return '<span style="color: green">' + '充值成功' + '</span>';
                    case '3':
                        return '<span style="color: red">' + '充值失败' + '</span>';
                    default:
                        return text;
                }
            }
        },
        propertyNames: {
            a_id: '订单编号',
            b_username: '用户名',
            c_phone: '号码',
            d_price: '订单金额',
            e_effectType: '到账类型',
            f_province: '省份',
            g_externalId: '外部单号',
            h_productId: '商品代码',
            i_productName: '商品名称',
            j_payState: '支付状态',
            k_payFailedReason: '支付失败原因',
            l_payId: '支付单号',
            m_refundId: '退款单号',
            n_rechargeId: '充值单号',
            o_rechargeState: '充值状态',
            p_rechargeFailedReason: '充值失败原因',
            q_notifyUrl: '回调地址',
            r_createTime: '创建时间',
            s_payTime: '支付时间',
            t_rechargeTime: '充值时间',
            u_rechargeEndTime: '充值结束时间',
            v_supName: "供货商"
        },
        viewConfig : {
            forceFit: true,
            scrollOffset: 2 // the grid will never have scrollbars
        }
    });

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
                    gridPanel.checkinRechargeStatusBtn.setDisabled(sm.getCount() < 1);
                    gridPanel.setToRechargeFailedBtn.setDisabled(sm.getCount() < 1);
                }
            }
        }),
        tbar: tbar,
        columns: gridColumns,
        bbar: pagingbar,
        listeners: {
            rowclick: function(grid, rowIndex, e){
                var record = grid.store.getAt(rowIndex);
                detailGrid.setSource({
                    a_id: record.get("id")||"",
                    b_username: record.get("username")||"",
                    c_phone: record.get("phone")||"",
                    d_price: record.get("price")||"",
                    e_effectType: record.get("effectType"),
                    f_province: record.get("province")||"",
                    g_externalId: record.get("externalId")||"",
                    h_productId: record.get("productId")||"",
                    i_productName: record.get("productName")||"",
                    j_payState: record.get("payState"),
                    k_payFailedReason: record.get("payFailedReason")||"",
                    l_payId: record.get("payId")||"",
                    m_refundId: record.get("refundId")||"",
                    n_rechargeId: IS_ADMIN ? record.get("rechargeId")||"" : null,
                    o_rechargeState: record.get("rechargeState"),
                    p_rechargeFailedReason: record.get("rechargeFailedReason")||"",
                    q_notifyUrl: record.get("notifyUrl")||"",
                    r_createTime: record.get("createTime")||"",
                    s_payTime: record.get("payTime")||"",
                    t_rechargeTime: record.get("rechargeTime")||"",
                    u_rechargeEndTime: record.get("rechargeEndTime")||"",
                    v_supName : record.get("supName")|| ""
                });
            }
        }
    });

    var cfg = {
        closable: true,
        autoScroll: true,
        layout: 'border',
        margins: '35 5 5 0',
        containerScroll: true,
        items: [{
                height: 98,
                maxHeight: 80,
                border: false,
                split: true,
                collapseMode: 'mini',
                region: 'north',
                layout: 'border',
                items: [formPanel]
            },
            gridPanel,
            detailGrid
        ]
    };

    // 设置为成员属性
    this.formPanel = formPanel;
    this.gridPanel = gridPanel;
    this.store = store;

    var allConfig = Ext.applyIf(config || {}, cfg);
    OrderListPanel.superclass.constructor.call(this, allConfig);
};

Ext.extend(OrderListPanel, Ext.Panel, {
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

            var btn = Ext.getCmp("OrderListSearchBtn");
            btn.disabled = true;
            this.loadGridData(params, function () {
                btn.disabled = false;
            })
        }
        ;
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
                if (!success) {
                    window.location.reload(true);
                }
                // do noting
                next();
            }
        });
    },

    /**
     * 充值核实按钮事件
     */
    checkinRechargeStatus: function () {
        var s = this.gridPanel.getSelectionModel().getSelections();
        var ids = [];
        for (var i = 0, r; r = s[i]; i++) {
            ids.push(r.get("id"));
        }

        if (ids.length == 0) {
            return;
        }

        var me = this;

        Ext.Ajax.request({
            url: Desktop.contextPath + "/api/order/checkRechargeState",
            method: "POST",
            params: {
                ids: ids
            },
            callback: function (option, success, resp) {
                var result = Ext.decode(resp.responseText);
                if (success && resp.status === 200 && result.success) {
                    me.store.load();
                    return;
                }
                console.warn("checkRechargeState failed");
                Ext.Msg.show({
                    title: '操作失败',
                    msg: result.message,
                    buttons: Ext.Msg.OK,
                    icon: Ext.MessageBox.ERROR
                });
            }
        });

    },

    /**
     * 确认失败按钮事件
     */
    setToRechargeFailed: function () {
        var s = this.gridPanel.getSelectionModel().getSelections();
        var ids = [];
        for (var i = 0, r; r = s[i]; i++) {
            ids.push(r.get("id"));
        }

        if (ids.length == 0) {
            return;
        }

        var me = this;

        Ext.MessageBox.show({
            title: '确认',
            msg: '请确认是将选中的' + ids.length + '条记录设置为失败？失败订单将会自动进行退款！',
            width:300,
            buttons: Ext.MessageBox.YESNO,
            multiline: true,
            fn: function (buttonId, text, opt) {
                if (buttonId === 'yes') {
                    Ext.Ajax.request({
                        url: Desktop.contextPath + "/api/order/setToRechargeFailed",
                        method: "POST",
                        params: {
                            failedReason: text,
                            ids: ids
                        },
                        callback: function (option, success, resp) {
                            var result = Ext.decode(resp.responseText);
                            if (success && resp.status === 200 && result.success) {
                                me.store.load();
                                return;
                            }
                            console.warn("setToRechargeFailed failed");
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

Ext.reg('tab.orderlist', OrderListPanel);

Ext.apply(Ext.form.VTypes, {
    dateVtype: function (val, field) {
        var date = Date.parseDate(val, "Y-m-d H:i:s");
        return date ? true : false;
    },
    dateVtypeText: '请使用"yyyy-MM-dd HH:mm:ss"的时间格式'
});

