/**
 * <p>Preference and Settings Management</p>
 *
 * <p>The Model Viewer features a sophisticated, metadata-driven settings system that integrates
 * application-level state with the 3D Engine configuration using Android's SharedPreferences.</p>
 *
 * <h3>Core Components</h3>
 * <ul>
 *     <li><b>SettingsManager:</b> The central controller that manages application settings state (AppSettings),
 *     coordinates hydration of technical metadata, and handles persistence.</li>
 *     <li><b>AppSettings:</b> A managed Bean that holds application-level properties like Language, Theme,
 *     API Keys, and AI model selection.</li>
 *     <li><b>SettingsFragment:</b> The presentation layer that dynamically renders Android Preferences based on
 *     hydrated Bean metadata.</li>
 * </ul>
 *
 * <h3>How it Works</h3>
 * <p>Properties annotated with <code>@BeanProperty</code> are automatically discovered. The system uses a
 * "Hydration" process to decorate technical identifiers with localized UI strings from resources.</p>
 *
 * <p><b>Convention:</b> Technical names are derived from class names (snake_case) or explicit <code>@Bean</code> names.
 * Keys are generated as <code>className.propertyName</code>.</p>
 *
 * <h3>Localization (i18n)</h3>
 * <p>Labels, descriptions, and list values are localized in <code>strings.xml</code> and <code>arrays.xml</code>
 * using the following format:</p>
 *
 * <ul>
 *     <li><b>Bean Label:</b> <code>bean_&lt;beanName&gt;_label</code></li>
 *     <li><b>Property Label:</b> <code>property_&lt;beanName&gt;_&lt;propertyName&gt;_label</code></li>
 *     <li><b>Property Description:</b> <code>property_&lt;beanName&gt;_&lt;propertyName&gt;_description</code></li>
 *     <li><b>List Values:</b> <code>property_&lt;beanName&gt;_&lt;propertyName&gt;_values</code> (Technical codes)</li>
 *     <li><b>List Labels:</b> <code>property_&lt;beanName&gt;_&lt;propertyName&gt;_values_descriptions</code> (UI names)</li>
 * </ul>
 *
 * <p><i>"Simplicity is the ultimate sophistication - but don't fight the Universe (O.S.)"</i></p>
 */
package org.the3deer.android.viewer.settings;
