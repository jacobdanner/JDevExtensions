package com.jacobd.jdev.ext.groovy.macro;

import oracle.ide.Context;
import oracle.ide.externaltools.macro.MacroExpander;

/**
 * @author jdanner
 */
public class GroovyHomeMacro extends MacroExpander
{
    @Override
    public String expand(Context context) {
        return System.getenv("GROOVY_HOME");
    }
}
