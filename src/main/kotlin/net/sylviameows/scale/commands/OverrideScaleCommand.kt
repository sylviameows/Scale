package net.sylviameows.scale.commands

import net.sylviameows.kitti.api.commands.Options


class OverrideScaleCommand : ScaleCommand() {
    override val name: String = "oscale"
    override val min = 0.1;
    override val max = 16.0;

    override fun options(options: Options): Options {
        return super.options(options).permission("scale.override")
    }


}