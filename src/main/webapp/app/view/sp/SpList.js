/**
 * Created by angus
 */
SpListPanel = function (config) {

    var serviceUrl = Desktop.contextPath + "/api/sp/search";
    var me = this;

    var dataGridPanel;

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
        items: [{
            x: 10,
            y: 12,
            width: 60,
            xtype: 'label',
            text: '商品编号：'
        },
            {
                x: 70,
                y: 12,
                width: 90,
                fieldLabel: '商品编号',
                name: 'productId',
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
            },{
                x: 10+160,
                y: 12,
                width: 60,
                xtype: 'label',
                text: '商品名称：'
            },
            {
                x: 10+220,
                y: 12,
                width: 130,
                fieldLabel: '商品名称',
                name: 'productName',
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
            },{
                x: 10+160+200,
                y: 12,
                width: 90,
                xtype: 'label',
                text: '供货商编号：'
            },
            {
                x: 10+220+220,
                y: 12,
                width: 130,
                fieldLabel: '供货编号',
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
            },{
                x: 10,
                y: 12+30,
                width: 90,
                xtype: 'label',
                text: '供货商名称：'
            },
            {
                x: 10+60,
                y: 12+30,
                width: 130,
                fieldLabel: '供货商名称',
                name: 'supName',
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
            },{
                x: 10+200,
                y: 12+30,
                width: 60,
                xtype: 'label',
                text: '使用状态：'
            },{
                x: 70+210,
                y: 12+30,
                width: 60,
                xtype: 'combo',
                typeAhead: true,
                triggerAction: 'all',
                lazyRender: true,
                mode: 'local',
                hiddenName: 'status',
                store: new Ext.data.ArrayStore({
                    id: 0,
                    fields: [
                        'id',
                        'text'
                    ],
                    data: [[0, '关闭'], [1, '启用']]
                }),
                forceSelection: true,
                //editable :false,
                emptyText: '请选择',
                valueField: 'id',
                displayField: 'text'
            },{
                x: 690,
                y: 10,
                width: 70,
                xtype: 'button',
                id: 'spListListSearchBtn',
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
        fields: ['productId', 'productName', 'supId', 'supName', 'status', 'weight'],
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
    tbar = [{
        text: '添 加',
        iconCls: 'icon-user-add',
        scope: this,
        handler: addBtn
    },{
        text: '修 改',
        iconCls: 'icon-user-edit',
        ref: '../updateBtn',
        scope: this,
        handler: updateBtn
    }];

    var gridColumns = [];

    gridColumns.push(new Ext.grid.RowNumberer({width: 25}));
    gridColumns.push({id: 'productId', align: 'center', header: "商品代码", width: 90, sortable: true, dataIndex: 'productId'});
    gridColumns.push({id: 'productName', align: 'center', header: "商品名称", width: 200, sortable: true, dataIndex: 'productName'});
    gridColumns.push({id: 'supId', align: 'center', header: "供货商编号", width: 100, sortable: true, dataIndex: 'supId'});
    gridColumns.push({id: 'supName', align: 'center', header: "供货商名称", width: 200, sortable: true, dataIndex: 'supName'});
    gridColumns.push({id: 'status', align: 'center', header: "状态", width: 140, sortable: true, dataIndex: 'status',
            renderer: function (text) {
                if(text == 1){
                    return '启用';
                }else{
                    return '关闭';
                }
            }});
    gridColumns.push({id: 'weight', align: 'center', header: "权重", width: 140, sortable: true, dataIndex: 'weight'});

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
            e_status: function (text) {
                if(text == 1){
                    return '启用';
                }else{
                    return '关闭';
                }
            }
        },
        propertyNames: {
            a_productId: '商品代码',
            b_productName: '商品名称',
            c_supId: '供货商编号',
            d_supName: '供货商名称',
            e_status: '供货商状态'
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
            msg: "努力加载数据中，请稍后...gridPanel"
        },
        store: store,
        sm: new Ext.grid.RowSelectionModel({
            //singleSelect: true,
            listeners: {
                scope: this,
                selectionchange: function (sm) {
                    //gridPanel.checkinRechargeStatusBtn.setDisabled(sm.getCount() < 1);
                    //gridPanel.setToRechargeFailedBtn.setDisabled(sm.getCount() < 1);
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
                    a_productId: record.get("productId")||"",
                    b_productName: record.get("productName")||"",
                    c_supId: record.get("supId")||"",
                    d_supName: record.get("supName")||"",
                    e_status: record.get("status")||""
                });
            },
            rowdblclick: function(grid, rowIndex, event){
                var record = grid.store.getAt(rowIndex);
                showEditPage(this,record);


            }
        }
    });


    function addBtn(){
        var win = Ext.getCmp("AddSpWin");
        var me = this;
        if (!win) {
            win = new AddSpWindow({
                id: 'AddSpWin',
                modal: true,
                callback: function(){
                    this.gridPanel.store.load();
                }
            });
        }
        win.show();
    }

    function updateBtn(){
        var s = this.gridPanel.getSelectionModel().getSelections();

        if (s.length != 1) {
            Ext.Msg.show({
                title: '警告',
                msg: "只能选择一条数据！",
                buttons: Ext.Msg.OK,
                icon: Ext.MessageBox.WARNING
            });
            return;
        }

        var win = Ext.getCmp("EditSpWin");
        if (!win) {
            win = new EditSpWindow({
                id: 'EditSpWin',
                modal: true,
                data: s[0],
                callback: function(){
                    this.gridPanel.store.load();
                }
            });
        }
        win.show();
    }

    function showEditPage(panel , record){
        var win = Ext.getCmp("EditSpWin");
        if (!win) {
            win = new EditSpWindow({
                id: 'EditSpWin',
                modal: true,
                data: record,
                callback: function(){
                    panel.store.load();
                }
            });
        }
        win.show();
    }
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
    SpListPanel.superclass.constructor.call(this, allConfig);
};

Ext.extend(SpListPanel, Ext.Panel, {
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
            var params = frm.getFieldValues();
            params.start = 0;
            params.limit = Desktop.pageSize;

            var btn = Ext.getCmp("spListListSearchBtn");
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
    }
});

Ext.reg('tab.splist', SpListPanel);

Ext.apply(Ext.form.VTypes, {
    dateVtype: function (val, field) {
        var date = Date.parseDate(val, "Y-m-d H:i:s");
        return date ? true : false;
    },
    dateVtypeText: '请使用"yyyy-MM-dd HH:mm:ss"的时间格式'
});

