/**
 * Created by angus
 */
UserSupLimitPanel = function (config) {

    var serviceUrl = Desktop.contextPath + "/api/usersuplimit/search";
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
            text: '用户名：'
        },
            {
                x: 70,
                y: 12,
                width: 90,
                fieldLabel: '用户名',
                name: 'userName',
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
                x: 200,
                y: 12+30,
                width: 90,
                xtype: 'label',
                text: '运营商：'
            },
            {
                x: 200+60,
                y: 12+30,
                width: 130,
                fieldLabel: '运营商',
                name: 'operator',
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
        fields: ['userName', 'supId', 'limitNum', 'current','operator'],
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
    gridColumns.push({id: 'userName', align: 'center', header: "用户名", width: 90, sortable: true, dataIndex: 'userName'});
    gridColumns.push({id: 'supId', align: 'center', header: "供货商编号", width: 100, sortable: true, dataIndex: 'supId'});
    gridColumns.push({id: 'operator', align: 'center', header: "运营商", width: 100, sortable: true, dataIndex: 'operator'});
    gridColumns.push({id: 'limitNum', align: 'center', header: "限制数量", width: 140, sortable: true, dataIndex: 'limitNum'});
    gridColumns.push({id: 'current', align: 'center', header: "当前数量", width: 140, sortable: true, dataIndex: 'current'});

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
            rowdblclick: function(grid, rowIndex, event){
                var record = grid.store.getAt(rowIndex);
                showEditPage(this,record);


            }
        }
    });


    function addBtn(){
        var win = Ext.getCmp("AddUserSupLimitWin");
        var me = this;
        if (!win) {
            win = new AddUserSupLimitWindow({
                id: 'AddUserSupLimitWin',
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

        var win = Ext.getCmp("EditUserSupLimitWin");
        if (!win) {
            win = new EditUserSupLimitWindow({
                id: 'EditUserSupLimitWin',
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
        var win = Ext.getCmp("EditUserSupLimitWin");
        if (!win) {
            win = new EditUserSupLimitWindow({
                id: 'EditUserSupLimitWin',
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
            gridPanel
        ]
    };

    // 设置为成员属性
    this.formPanel = formPanel;
    this.gridPanel = gridPanel;
    this.store = store;

    var allConfig = Ext.applyIf(config || {}, cfg);
    UserSupLimitPanel.superclass.constructor.call(this, allConfig);
};

Ext.extend(UserSupLimitPanel, Ext.Panel, {
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

Ext.reg('tab.usersuplimit', UserSupLimitPanel);

Ext.apply(Ext.form.VTypes, {
    dateVtype: function (val, field) {
        var date = Date.parseDate(val, "Y-m-d H:i:s");
        return date ? true : false;
    },
    dateVtypeText: '请使用"yyyy-MM-dd HH:mm:ss"的时间格式'
});

