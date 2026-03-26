# Settings

The Preference Screen is built as follows

- There will be 1 preference category for every feature category or bean category found in the bean factory
- If the bean is experimental, the menu will show the item as "My Component (Experimental)"
- If the feature is experimental, all the beans in the feature will show the item as "My Component (Experimental)"

## Components & Feature

- The Components are the actual implementation of the Features
- The Feature annotation is to be put in the java package implementation of the feature
- The Feature annotation is to set common properties if all beans in the same package share the same attributes

## Preference Keys

The key for each preference is constructed as follows:
`<className>.<propertyName>`

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
