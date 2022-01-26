package marble.entity.components;

import marble.entity.Entity;

public abstract class Component {

    protected Entity entity = null;

    public abstract void render();

    public abstract void cleanUp();

    public void setEntity(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return this.entity;
    }
}