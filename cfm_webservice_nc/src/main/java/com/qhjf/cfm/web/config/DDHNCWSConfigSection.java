//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package  com.qhjf.cfm.web.config;

import com.qhjf.cfm.web.config.AbstractConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.IConfigSectionType.DDHConfigSectionType;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDHNCWSConfigSection extends AbstractConfigSection {
    private static final Logger log = LoggerFactory.getLogger(DDHNCWSConfigSection.class);
    private String pushWsdl;
    private String authorityWsdl;
    private String authorityUserName;
    private String authorityPassword;

    public DDHNCWSConfigSection() {
        Properties pros = reader.getSection(this.getSectionName());
        if (pros != null && pros.size() > 0) {
            this.pushWsdl = this.getAndValidateNoNullItem(pros, "pushWsdl");
        } else {
            this.addErrMsg("[%s] 未配置或配置项为空\r\n", new Object[]{this.getSectionName()});
        }

    }

    public String getSectionName() {
        return "DDHNCWSConfigSection";
    }

    public IConfigSectionType getSectionType() {
        return DDHConfigSectionType.DDHNCWS;
    }

    public String getPushWsdl() {
        return this.pushWsdl;
    }

    public String getAuthorityWsdl() {
        return this.authorityWsdl;
    }

    public String getAuthorityUserName() {
        return this.authorityUserName;
    }

    public String getAuthorityPassword() {
        return this.authorityPassword;
    }

    public static void main(String[] args) {
        DDHNCWSConfigSection section = new DDHNCWSConfigSection();
        if (section.isValidate()) {
            log.info(section.getPushWsdl());
            log.info(section.getAuthorityWsdl());
        } else {
            log.error(section.getErrMsg());
        }

    }
}
