package net.sylviameows.scale.commands

import net.sylviameows.scale.commands.api.Options

class OverrideScaleCommand : ScaleCommand() {
    override val name: String = "oscale"
    override val min = 0.1;
    override val max = 16.0;

    override fun options(): Options {
        return super.options().permission("scale.override")
    }


}