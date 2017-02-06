package com.atlassian.tutorial.ao.todo.upgrade.v1; // (1)

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;

public final class TodoUpgradeTask001 implements ActiveObjectsUpgradeTask
{
    @Override
    public ModelVersion getModelVersion()
    {
        return ModelVersion.valueOf("1"); // (2)
    }

    @Override
    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) // (3)
    {
        ao.migrate(Todo.class); // (4)

        for (Todo todo : ao.find(Todo.class)) // (5)
        {
            todo.setUserName("admin");
            todo.save();
        }
    }
}
