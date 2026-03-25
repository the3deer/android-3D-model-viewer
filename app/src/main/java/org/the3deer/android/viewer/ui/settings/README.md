# Settings

The Preference Screen is built as follows

- There is 1 category for every feature
- There is 1 category for every bean

## Preference Keys

The key for each preference is constructed as follows:
`<className>.<propertyName>`

## Model

    - Bean
    |- BeanProperty ( field or method )
    |   |- name
    |   |- description
    |   |- valueNames


## Dependencies

- BeanFactory instance
- Annotated @Bean
- Annotated @BeanProperty

## Storing Preferences

Preferences are stored using the following ID strategy:
1. If `valueNames` is provided, the name is used as the stored ID (e.g., "Gray").
2. If values are `String` or `Number`, the value itself is used.
3. Otherwise, the index is used as a fallback.

## API

### Special Properties

#### The `enabled` property
If a property named `enabled` (boolean) exists in a bean, it is treated as a **master toggle** for that component. 
- It will be displayed at the top of the component section.
- All other properties within the same bean will automatically depend on it (i.e., they will be disabled in the UI if `enabled` is set to `false`).

```java
@BeanProperty
protected boolean enabled = true;
```

### Custom Property Values


    @BeanProperty(name = "Background Color", description = "Select the default color for 3D models", valueNames = {"White", "Gray", "Black"})
    private float[] backgroundColor = Constants.COLOR_GRAY;

    @BeanProperty(name = "backgroundColor", valueNames = {"White", "Gray", "Black"})
    public List<float[]> getBackgroundColorValues() {
        return Arrays.asList(Constants.COLOR_WHITE, Constants.COLOR_GRAY, Constants.COLOR_BLACK);
    }

### Delegated Property Values

    @BeanProperty(name = "Property X", description = "My delegated property X")
    public void setSomeFlag(boolean enabled){
        delegate.setSomeFlag(enabled);
    }

    @BeanProperty(description = "My delegated property X")
    public boolean isSomeFlag(){
        delegate.isSomeFlag();
        return false;
    }
