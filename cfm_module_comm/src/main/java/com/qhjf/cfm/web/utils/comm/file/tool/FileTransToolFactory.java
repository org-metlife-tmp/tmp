package com.qhjf.cfm.web.utils.comm.file.tool;

import com.qhjf.cfm.web.config.*;

/**
 * 文件保存、读取工具工厂
 * @author CHT
 *
 */
public class FileTransToolFactory {

	private final static AttachmentConfigSection mgr = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Attachment);
	private static FileTransTool fileTransTool = null;
	
	public static FileTransTool getInstance(){
		
		if (null != fileTransTool) {
			return fileTransTool;
		}

		IConfigSectionType.AttachmenConfigSectionType mode = mgr.getMode();
		if(mode.equals(IConfigSectionType.AttachmenConfigSectionType.Mongo)){
			fileTransTool = MongoFileTransTool.getInstance((MongoConfigSection) mgr.getChildConfig());
		}else if(mode.equals(IConfigSectionType.AttachmenConfigSectionType.FileSave)){
			fileTransTool = SystemFileTransTool.getInstance((FileSaveConfigSection)mgr.getChildConfig());
		}
		return fileTransTool;
	}
}
