package org.zbinfinn.wecode.clipboards;

import net.minecraft.text.Text;

public interface Value {
    Text render();
    String value();
    String data();
}
