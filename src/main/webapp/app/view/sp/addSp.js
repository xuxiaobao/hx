AddSpWindow = function (config) {

    var formPanel = new Ext.FormPanel({
        region: 'center',
        labelWidth: 75, // label settings here cascade unless overridden
        url: Desktop.contextPath + '/api/sp/create',
        border: false,
        //title: 'Simple Form',
        bodyStyle: 'padding:10px 10px 0px 10px', //padding：上、右、下、左
        //width: 350,
        defaults: {anchor: '-20', msgTarget: 'side', allowBlank: false},
        defaultType: 'textfield',
        items: [{
                fieldLabel: '商品编号',
                name: 'productId'
            }, {
                fieldLabel: '供货商编号',
                name: 'supId'
            }, {
            fieldLabel: '权重',
            name: 'weight'
        }, {
            xtype: 'radiogroup',
            fieldLabel: '启用状态',
            items: [
                {boxLabel: '关闭', name: 'status', margins: '0 5 0 5', inputValue: 0, checked: true},
                {boxLabel: '启用', name: 'status', margins: '0 5 0 5', inputValue: 1}
            ]
        }
        ]
    });

    this.resultPanel = new Ext.Panel({
        bodyStyle: 'padding:10px 10px 10px 10px', //padding：上、右、下、左
        region: 'south',
        height: 30,
        border: false,
        html: ''
    });

    var cfg = {
        title: '添加对接关系',
        width: 385,
        height: 310,
        closable: true,
        autoScroll: true,
        iconCls: 'icon-user-add',
        //closeAction: 'hide',
        resizable: false,
        shim: false,
        animCollapse: false,
        constrainHeader: true,
        layout: 'border',
        //margins: '35 5 5 0',
        containerScroll: true,
        items: [formPanel, this.resultPanel],
        buttons: [{
            text: '确  认',
            scope: this,
            handler: function () {
                this.submitForm();
            }
        }, {
            text: '关  闭',
            scope: this,
            handler: function () {
                this.close();
            }
        }]
    };

    this.formPanel = formPanel;

    var allConfig = Ext.applyIf(config || {}, cfg);
    AddSpWindow.superclass.constructor.call(this, allConfig);
};

Ext.extend(AddSpWindow, Ext.Window, {
    submitForm: function () {
        if (this.formPanel.getForm().isValid()) {
            var me = this;
            Ext.Msg.show({
                title: '确认',
                msg: '请确认是否保存?',
                scope: this,
                buttons: Ext.Msg.YESNO,
                icon: Ext.MessageBox.QUESTION,
                fn: function (buttonId, text, opt) {
                    if (buttonId === 'yes') {
                        var frm = me.formPanel.getForm();
                        var resultPanel = me.resultPanel;

                        Ext.Ajax.request({
                            url: frm.url,
                            method: "POST",
                            jsonData: frm.getValues(),
                            params: {},
                            callback: function (option, success, resp) {
                                var result = Ext.decode(resp.responseText);
                                var msg = "";
                                if (success && resp.status === 200) {
                                    if (result.success) {
                                        msg = "<div style='color: green; text-align: center;'>" + result.message + "</div>";
                                    }else{
                                        msg = "<div style='color: red; text-align: center;'>" + result.message + "</div>";
                                    }

                                } else {
                                    console.warn("generate memberName failed");
                                    msg = "<div style='color: red; text-align: center;'>" + result.message + "</div>";
                                }
                                resultPanel.update(msg);
                                me.callback();
                            }
                        });
                    }
                }
            });
        }

    }
});

Ext.reg('win.addSp', AddSpWindow);

Ext.apply(Ext.form.VTypes, {
    discountVtype: function (val, field) {
        var str = val.toString();
        return val <= 1 && str.length <= 5;
    },
    discountVtypeText: '折扣不能大于1，且小数点后最多3位'
});