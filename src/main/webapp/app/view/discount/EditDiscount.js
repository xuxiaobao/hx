EditDiscountWindow = function (config) {
    var userSupLimit = config.data;

    var formPanel = new Ext.FormPanel({
        region: 'center',
        labelWidth: 95, // label settings here cascade unless overridden
        url: Desktop.contextPath + '/api/userDiscount/update',
        border: false,
        //title: 'Simple Form',
        bodyStyle: 'padding:10px 10px 0px 10px', //padding：上、右、下、左
        //width: 350,
        defaults: {anchor: '-20', msgTarget: 'side', allowBlank: false},
        defaultType: 'textfield',
        items: [
            {
                fieldLabel: '用户名',
                readOnly: true,
                cls: 'x-item-disabled',
                name: 'userName',
                value: userSupLimit.get("userName")
            },{
                fieldLabel: '运营商',
                readOnly: true,
                cls: 'x-item-disabled',
                name: 'pid',
                value: userSupLimit.get("pid")
            },{
                fieldLabel: '折扣',
                name: 'discount',
                value: userSupLimit.get("discount")
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
        title: '修改商户供货商限制数量',
        width: 405,
        height: 420,
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
    EditDiscountWindow.superclass.constructor.call(this, allConfig);
};

Ext.extend(EditDiscountWindow, Ext.Window, {
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
                                    console.warn("update failed");
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

Ext.reg('win.editDiscount', EditDiscountWindow);

Ext.apply(Ext.form.VTypes, {
    discountVtype: function (val, field) {
        var str = val.toString();
        return val <= 1 && str.length <= 5;
    },
    discountVtypeText: '折扣不能大于1，且小数点后最多3位'
});