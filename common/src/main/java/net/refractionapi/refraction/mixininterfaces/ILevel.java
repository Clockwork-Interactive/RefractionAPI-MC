package net.refractionapi.refraction.mixininterfaces;

import net.refractionapi.refraction.feature.atda.IAtdaProvider;
import net.refractionapi.refraction.feature.task.LevelTasks;

public interface ILevel extends IAtdaProvider {
    LevelTasks tasks();
}
