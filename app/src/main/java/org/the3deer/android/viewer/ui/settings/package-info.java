/**
 * <p>Preference Management</p>
 * <p>The Model Viewer is integrated with the Android's SharedPreferences.</p>
 * <p>
 * Beans: All beans annotated with <code>@BeanProperty</code> are automatically mapped to SharedPreferences.
 * Keys: Preference keys are automatically generated as <code><className>.<propertyName>.</code>
 * Storage: Property values are stored based on their name. valueNames in the annotation allows populating the <codeListPreference</code>.
 *
 * <p>i18n</p>
 * The values can be localized in the arrays.xml using the following format for the name property:
 *
 * <p>
 *     Format:
 * <code>
 *     name="property_<beanName>_<beanPropertyName>_descriptions"
 * </code>
 * </p>
 * <p>
 *     Example for skybox german localization:
 * <code>
 * <string-array name="property_skybox_drawer_skybox_values_descriptions">
 * <item>Keiner</item>
 * <item>Meer</item>
 * <item>Sand</item>
 * <item>Dynamisch</item>
 * </code>
 * </p>
 */
package org.the3deer.android.viewer.ui.settings;