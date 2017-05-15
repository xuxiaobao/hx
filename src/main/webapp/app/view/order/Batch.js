/**
 * Created by angus
 */
BatchPanel = function(config) {

	var form = new Ext.form.FormPanel({
		autoScroll: true,
        containerScroll: true,
        bodyStyle: 'padding:5px 5px 0',
        region: 'north',
        labelWidth: 75,
        border: false,
        //defaultType: 'textfield',
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
		//renderTo : 'file',
		labelAlign : 'right',
		title : '文件上传',
		frame : true,
		url : Desktop.contextPath + '/openapi/order/batch', 
		width : 500,
		height : 100,
		fileUpload : true,
		items : [
	    {
			 y: 5,
             width: 60,
			 html:"<a href='"+Desktop.contextPath+"/template/template.xls'>模板下载</a>"
		},{
			x: 60,
			xtype : 'textfield',
			fieldLabel : '文件名',
			name : 'file',
			inputType : 'file' 
		}
		],

		buttons : [ {
			text : '上传',
			handler : function() {
				form.getForm().submit({
					success : function(form, action) {
						Ext.Msg.alert('信息', action.result.data);
					},
					failure : function() {
						Ext.Msg.alert('错误', '文件上传失败');
					}
				});
			}
		} ]
	});

	var cfg = {
		closable : true,
		autoScroll : true,
		layout : 'border',
		margins : '35 5 5 0',
		containerScroll : true,
		items : [ {
			height : 98,
			maxHeight : 80,
			border : false,
			split : true,
			collapseMode : 'mini',
			region : 'center',
			layout : 'border',
			items : [ form ]
		} ]
	};
	var allConfig = Ext.applyIf(config || {}, cfg);
	BatchPanel.superclass.constructor.call(this, allConfig);
};

Ext.extend(BatchPanel, Ext.Panel);

Ext.reg('tab.batch', BatchPanel);
