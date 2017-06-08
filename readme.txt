android 代码说明

提供基本功能,不参与业务流程
com.ou.base
	BoardConfig.java 				板数量,灯数量,灯组信息
	CalInfo.java     				校验信息
	CallBack.java  					回调接口,通知usb连接状态
	CalMath.java     				算法
	Device.java      				USB设备接口
	Function.java   				所有USB业务方法的集合
	HardwareSignal.java				硬件信号灯
	ShortCutPointGroup.java         一组快捷键按键点集合
	ShortCutReport.java             所有快捷键按键点集合

java基本功能和常数
com.ou.common
	ComFunc.java 					java基本功能和常数
	Constant.java					宏定义

业务线程
com.ou.thread
	CalPointThread.java 			获取点的线程
	DetectUsbThread.java			获取USB状态线程
	HardwareTestWorkThread.java     获取硬件状态线程
	UpgradeThread.java				更新固件线程

窗体	
com.ou.ui
	CalActivity.java 				校准坐标窗口
	FileSelectorActivity.java		文件选择器窗口
	HardwareTestAcitivity			硬件检测窗口
	KeyCntDialog.java				提示快捷键数量窗口
	MainActivity.java				所有功能选择主窗口
	ProgressDialog.java				升级固件进度对话框
	SettingDialog.java  			板子设置对话框
	ShortCutActivity.java			快捷键设置窗口
	UIMessageHandler.java			消息显示
	
视图
com.ou.view
	HardwareTestView.java			硬件检测视图
	ProgressView.java				固件升级视图
	ShortCutView.java				快捷键设置视图
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	