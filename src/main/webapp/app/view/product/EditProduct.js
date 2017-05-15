EditProductWindow = function (config) {

    var product = config.data || {};
    var isAdd = config.isAdd || false;
    var initData =config.initData || {};
    var provinces = [];
    var range=[];
    var face=[];
    var flowValue= [];
    var effectType=[];
    var expiredDate=[];
    var operator=[];
    for(var i=0;i<initData.length;i++){
        if(initData[i].propName=='省份'){
            for(var j=0;j<initData[i].propValueList.length;j++){
                var prop =[];
                prop.push(initData[i].propValueList[j].propValue);
                prop.push(initData[i].propValueList[j].propValue);
                provinces.push(prop);
            }
        }


        if(initData[i].propName=='生效范围'){
            for(var j=0;j<initData[i].propValueList.length;j++){
                var prop =[];
                prop.push(initData[i].propValueList[j].propValue);
                prop.push(initData[i].propValueList[j].propValue);
                range.push(prop);
            }
        }

        if(initData[i].propName=='运营商'){
            for(var j=0;j<initData[i].propValueList.length;j++){
                var prop =[];
                prop.push(initData[i].propValueList[j].propValue);
                prop.push(initData[i].propValueList[j].propValue);
                operator.push(prop);
            }
        }

        if(initData[i].propName=='面值'){
            for(var j=0;j<initData[i].propValueList.length;j++){
                var prop =[];
                prop.push(initData[i].propValueList[j].propValue);
                prop.push(initData[i].propValueList[j].propValue);
                face.push(prop);
            }
        }

        if(initData[i].propName=='流量值'){
            for(var j=0;j<initData[i].propValueList.length;j++){
                var prop =[];
                prop.push(initData[i].propValueList[j].propValue);
                prop.push(initData[i].propValueList[j].propValue);
                flowValue.push(prop);
            }
        }

        if(initData[i].propName=='生效日期'){
            for(var j=0;j<initData[i].propValueList.length;j++){
                var prop =[];
                prop.push(initData[i].propValueList[j].propValue);
                prop.push(initData[i].propValueList[j].propValue);
                effectType.push(prop);
            }
        }

        if(initData[i].propName=='流量有效期'){
            for(var j=0;j<initData[i].propValueList.length;j++){
                var prop =[];
                prop.push(initData[i].propValueList[j].propValue);
                prop.push(initData[i].propValueList[j].propValue);
                expiredDate.push(prop);
            }
        }
    }

    var formPanel = new Ext.FormPanel({
        region: 'center',
        labelWidth: 95, // label settings here cascade unless overridden
        url: isAdd ? Desktop.contextPath + '/api/product/create' : Desktop.contextPath + '/api/product/update',
        border: false,
        bodyStyle: 'padding:10px 10px 0px 10px', //padding：上、右、下、左
        //width: 350,
        defaults: {anchor: '-20', msgTarget: 'side', allowBlank: false},
        defaultType: 'textfield',
        items: [
		{
		    fieldLabel: '产品ID',
		    name: 'id',
		    readOnly : !isAdd,
		    value: product.id
		},
        {
            fieldLabel: '省份',
            xtype: 'combo',
            typeAhead: true,
            triggerAction: 'all',
            lazyRender: true,
            mode: 'local',
            hiddenName: 'province',
            store: new Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'id',
                    'text'
                ],
                data:[['全国','全国'],['省内','省内'],['北京','北京'],['天津','天津'],['上海','上海'],['重庆','重庆'],['河北','河北'],['河南','河南'],['云南','云南'],['辽宁','辽宁'],['黑龙江','黑龙江'],['湖南','湖南'],['安徽','安徽'],['山东','山东'],['新疆','新疆'],['江苏','江苏'],['浙江','浙江'],['江西','江西'],['湖北','湖北'],['广西','广西'],['甘肃','甘肃'],['山西','山西'],['内蒙古','内蒙古'],['陕西','陕西'],['吉林','吉林'],['福建','福建'],['贵州','贵州'],['广东','广东'],['海南','海南'],['青海','青海'],['西藏','西藏'],['四川','四川'],['宁夏','宁夏'],['湖南','湖南'],['台湾','台湾'],['香港','香港'],['澳门','澳门']]
            }),
            forceSelection: true,
            //editable :false,
            emptyText: '请选择',
            valueField: 'id',
            displayField: 'text',
            readOnly : !isAdd,
            value: product.province
        },{
            fieldLabel: '面值',
            //xtype: 'combo',
            //typeAhead: true,
            //triggerAction: 'all',
            //lazyRender: true,
            //mode: 'local',
            name: 'faceValue',
            value: product.faceValue
            /*store: new Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'id',
                    'text'
                ],
                data:face
            }),*/
            //forceSelection: true,
            //editable :false,
            //emptyText: '请选择',
            //valueField: 'id',
            //displayField: 'text'
        },{
            fieldLabel: '流量值',
            //xtype: 'combo',
            //typeAhead: true,
            //triggerAction: 'all',
            //lazyRender: true,
            //mode: 'local',
            name: 'flowValue',
            readOnly : !isAdd,
            value: product.flowValue
            /*store: new Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'id',
                    'text'
                ],
                data:flowValue
            }),*/
            //forceSelection: true,
            //editable :false,
            //emptyText: '请选择',
            //valueField: 'id',
            //displayField: 'text'
        }, {
                xtype: 'radiogroup',
                fieldLabel: '商品类型',
                anchor: '-130',
                readOnly : !isAdd,
                items: [
                    {boxLabel: '前向产品', name: 'type', margins: '0 5 0 5', inputValue: 'QX', checked: isAdd || product.type == 'QX'},
                    {boxLabel: '后向产品', name: 'type', margins: '0 5 0 5', inputValue: 'HX', checked: product.type == 'HX'}
                ]
            }, {
            fieldLabel: '运营商',
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
                    data:operator
                }),
                forceSelection: true,
                //editable :false,
                emptyText: '请选择',
                readOnly : !isAdd,
                valueField: 'id',
                displayField: 'text',
                value: product.operator
            },{
            fieldLabel: '生效范围',
            xtype: 'combo',
            mode: 'local',
            hiddenName: 'range',
            store: new Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'id',
                    'text'
                ],
                data: range
            }),
            value: product.range,
            forceSelection: true,
            //editable :false,
            emptyText: '请选择',
            valueField: 'id',
            readOnly : !isAdd,
            displayField: 'text'
        },{
            fieldLabel: '生效日期',
            xtype: 'combo',
            mode: 'local',
            hiddenName: 'effectType',
            store: new Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'id',
                    'text'
                ],
                data: effectType
            }),
            value: product.effectType,
            forceSelection: true,
            //editable :false,
            emptyText: '请选择',
            readOnly : !isAdd,
            valueField: 'id',
            displayField: 'text',
        },{
            fieldLabel: '流量有效期',
            xtype: 'combo',
            mode: 'local',
            hiddenName: 'expiredDate',
            store: new Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'id',
                    'text'
                ],
                data: expiredDate
            }),
            value:product.expiredDate,
            forceSelection: true,
            //editable :false,
            emptyText: '请选择',
            readOnly : !isAdd,
            valueField: 'id',
            displayField: 'text'
        },{
                xtype: 'radiogroup',
                fieldLabel: '商品状态',
                anchor: '-180',
                readOnly : !isAdd,
                items: [
                    {boxLabel: '启用', name: 'enabled', margins: '0 5 0 5', inputValue: true, checked: isAdd || product.enabled},
                    {boxLabel: '禁用', name: 'enabled', margins: '0 5 0 5', inputValue: false, checked: !isAdd && !product.enabled}
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
        title: isAdd ? '新增商品信息' : '修改商品信息',
        width: 405,
        height: 400,
        closable: true,
        autoScroll: true,
        iconCls: isAdd ? 'icon-add' : 'icon-edit',
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
    EditProductWindow.superclass.constructor.call(this, allConfig);
};

Ext.extend(EditProductWindow, Ext.Window, {
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
                                    console.warn("submit failed");
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

Ext.reg('win.editproduct', EditProductWindow);

Ext.apply(Ext.form.VTypes, {
    priceVtype: function (val, field) {
        var str = val.toString();

        return (str.length - str.indexOf(".")) < 5;
    },
    priceVtypeText: '商品价格小数点后最多3位'
});