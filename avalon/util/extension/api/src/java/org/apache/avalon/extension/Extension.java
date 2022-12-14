/* 
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * <p>Utility class that represents either an available "Optional Package"
 * (formerly known as "Standard Extension") as described in the manifest
 * of a JAR file, or the requirement for such an optional package.</p>
 *
 * <p>For more information about optional packages, see the document
 * <em>Optional Package Versioning</em> in the documentation bundle for your
 * Java2 Standard Edition package, in file
 * <code>guide/extensions/versioning.html</code>.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/04/06 07:42:48 $
 */
public final class Extension
{
    /**
     * Manifest Attribute Name object for EXTENSION_LIST.
     * @see java.util.jar.Attributes.Name#EXTENSION_LIST
     */
    public static final Attributes.Name EXTENSION_LIST = Attributes.Name.EXTENSION_LIST;

    /**
     * <code>Name</code> object for <code>Optional-Extension-List</code>
     * manifest attribute used for declaring optional dependencies on
     * installed extensions. Note that the dependencies declared by this method
     * are not required for the library to operate but if present will be used.
     * It is NOT part of the official "Optional Package" specification.
     *
     * @see <a href="http://java.sun.com/j2se/1.3/docs/guide/extensions/spec.html#dependnecy">
     *      Installed extension dependency</a>
     */
    public static final Attributes.Name OPTIONAL_EXTENSION_LIST =
        new Attributes.Name( "Optional-Extension-List" );

    /**
     * Manifest Attribute Name object for EXTENSION_NAME.
     * @see java.util.jar.Attributes.Name#EXTENSION_NAME
     */
    public static final Attributes.Name EXTENSION_NAME =
        Attributes.Name.EXTENSION_NAME;

    /**
     * Manifest Attribute Name object for SPECIFICATION_VERSION.
     * @see java.util.jar.Attributes.Name#SPECIFICATION_VERSION
     */
    public static final Attributes.Name SPECIFICATION_VERSION =
        Attributes.Name.SPECIFICATION_VERSION;

    /**
     * Manifest Attribute Name object for SPECIFICATION_VENDOR.
     * @see java.util.jar.Attributes.Name#SPECIFICATION_VENDOR
     */
    public static final Attributes.Name SPECIFICATION_VENDOR =
        Attributes.Name.SPECIFICATION_VENDOR;

    /**
     * Manifest Attribute Name object for IMPLEMENTATION_VERSION.
     * @see java.util.jar.Attributes.Name#IMPLEMENTATION_VERSION
     */
    public static final Attributes.Name IMPLEMENTATION_VERSION =
        Attributes.Name.IMPLEMENTATION_VERSION;

    /**
     * Manifest Attribute Name object for IMPLEMENTATION_VENDOR.
     * @see java.util.jar.Attributes.Name#IMPLEMENTATION_VENDOR
     */
    public static final Attributes.Name IMPLEMENTATION_VENDOR =
        Attributes.Name.IMPLEMENTATION_VENDOR;

    /**
     * Manifest Attribute Name object for IMPLEMENTATION_URL.
     * @see java.util.jar.Attributes.Name#IMPLEMENTATION_URL
     */
    public static final Attributes.Name IMPLEMENTATION_URL =
        Attributes.Name.IMPLEMENTATION_URL;

    /**
     * Manifest Attribute Name object for IMPLEMENTATION_VENDOR_ID.
     * @see java.util.jar.Attributes.Name#IMPLEMENTATION_VENDOR_ID
     */
    public static final Attributes.Name IMPLEMENTATION_VENDOR_ID =
        Attributes.Name.IMPLEMENTATION_VENDOR_ID;

    /**
     * Enum indicating that extension is compatible with other extension.
     */
    public static final Compatability COMPATIBLE =
        new Compatability( "COMPATIBLE" );

    /**
     * Enum indicating that extension requires an upgrade
     * of specification to be compatible with other extension.
     */
    public static final Compatability REQUIRE_SPECIFICATION_UPGRADE =
        new Compatability( "REQUIRE_SPECIFICATION_UPGRADE" );

    /**
     * Enum indicating that extension requires a vendor
     * switch to be compatible with other extension.
     */
    public static final Compatability REQUIRE_VENDOR_SWITCH =
        new Compatability( "REQUIRE_VENDOR_SWITCH" );

    /**
     * Enum indicating that extension requires an upgrade
     * of implementation to be compatible with other extension.
     */
    public static final Compatability REQUIRE_IMPLEMENTATION_UPGRADE =
        new Compatability( "REQUIRE_IMPLEMENTATION_UPGRADE" );

    /**
     * Enum indicating that extension is incompatible with
     * other extension in ways other than other enums
     * indicate). ie For example the other extension may have
     * a different ID.
     */
    public static final Compatability INCOMPATIBLE =
        new Compatability( "INCOMPATIBLE" );

    /**
     * The name of the optional package being made available, or required.
     */
    private String m_extensionName;

    /**
     * The version number (dotted decimal notation) of the specification
     * to which this optional package conforms.
     */
    private DeweyDecimal m_specificationVersion;

    /**
     * The name of the company or organization that originated the
     * specification to which this optional package conforms.
     */
    private String m_specificationVendor;

    /**
     * The unique identifier of the company that produced the optional
     * package contained in this JAR file.
     */
    private String m_implementationVendorID;

    /**
     * The name of the company or organization that produced this
     * implementation of this optional package.
     */
    private String m_implementationVendor;

    /**
     * The version number (dotted decimal notation) for this implementation
     * of the optional package.
     */
    private String m_implementationVersion;

    /**
     * The URL from which the most recent version of this optional package
     * can be obtained if it is not already installed.
     */
    private String m_implementationURL;

    /**
     * Return an array of <code>Extension</code> objects representing optional
     * packages that are available in the JAR file associated with the
     * specified <code>Manifest</code>.  If there are no such optional
     * packages, a zero-length array is returned.
     *
     * @param manifest Manifest to be parsed
     * @return the "available" extensions in specified manifest
     */
    public static Extension[] getAvailable( final Manifest manifest )
    {
        if( null == manifest )
        {
            return new Extension[ 0 ];
        }

        final ArrayList results = new ArrayList();

        final Attributes mainAttributes = manifest.getMainAttributes();
        if( null != mainAttributes )
        {
            final Extension extension = getExtension( "", mainAttributes );
            if( null != extension )
            {
                results.add( extension );
            }
        }

        final Map entries = manifest.getEntries();
        final Iterator keys = entries.keySet().iterator();
        while( keys.hasNext() )
        {
            final String key = (String)keys.next();
            final Attributes attributes = (Attributes)entries.get( key );
            final Extension extension = getExtension( "", attributes );
            if( null != extension )
            {
                results.add( extension );
            }
        }

        return (Extension[])results.toArray( new Extension[ results.size() ] );
    }

    /**
     * Retrieve the set of <code>Extension</code> objects that are available
     * by the specified Manifest objects. If there are no such optional
     * packages, a zero-length list is returned.
     *
     * @param manifests the manifests to scan
     * @return the extensions
     */
    public static Extension[] getAvailable( final Manifest[] manifests )
    {
        final ArrayList set = new ArrayList();

        for( int i = 0; i < manifests.length; i++ )
        {
            final Extension[] extensions = getAvailable( manifests[ i ] );
            for( int j = 0; j < extensions.length; j++ )
            {
                set.add( extensions[ j ] );
            }
        }

        return (Extension[])set.toArray( new Extension[ set.size() ] );
    }

    /**
     * Return the set of <code>Extension</code> objects representing optional
     * packages that are required by the application contained in the JAR
     * file associated with the specified <code>Manifest</code>.  If there
     * are no such optional packages, a zero-length list is returned.
     *
     * @param manifest Manifest to be parsed
     * @return the dependencies that are specified in manifes
     */
    public static Extension[] getRequired( final Manifest manifest )
    {
        return getListed( manifest, EXTENSION_LIST );
    }

    /**
     * Retrieve the set of <code>Extension</code> objects that are required
     * by the specified Manifest objects. If there are no such optional
     * packages, a zero-length list is returned.
     *
     * @param manifests the manifests to scan
     * @return the extensions
     */
    public static Extension[] getRequired( final Manifest[] manifests )
    {
        final ArrayList set = new ArrayList();

        for( int i = 0; i < manifests.length; i++ )
        {
            final Extension[] extensions = getRequired( manifests[ i ] );
            for( int j = 0; j < extensions.length; j++ )
            {
                set.add( extensions[ j ] );
            }
        }

        return (Extension[])set.toArray( new Extension[ set.size() ] );
    }

    /**
     * Return the set of <code>Extension</code> objects representing "Optional
     * Packages" that the application declares they will use if present. If
     * there are no such optional packages, a zero-length list is returned.
     *
     * @param manifest Manifest to be parsed
     * @return the optional dependencies that are specified in manifest
     */
    public static Extension[] getOptions( final Manifest manifest )
    {
        return getListed( manifest, OPTIONAL_EXTENSION_LIST );
    }

    /**
     * Add Extension to the specified manifest Attributes.
     *
     * @param attributes the attributes of manifest to add to
     * @param extension the extension
     */
    public static void addExtension( final Extension extension,
                                     final Attributes attributes )
    {
        addExtension( extension, "", attributes );
    }

    /**
     * Add Extension to the specified manifest Attributes.
     * Use the specified prefix so that dependencies can added
     * with a prefix such as "java3d-" etc.
     *
     * @param attributes the attributes of manifest to add to
     * @param extension the extension
     * @param prefix the name to prefix to extension
     */
    public static void addExtension( final Extension extension,
                                     final String prefix,
                                     final Attributes attributes )
    {
        attributes.putValue( prefix + EXTENSION_NAME,
                             extension.getExtensionName() );

        final String specificationVendor = extension.getSpecificationVendor();
        if( null != specificationVendor )
        {
            attributes.putValue( prefix + SPECIFICATION_VENDOR,
                                 specificationVendor );
        }

        final DeweyDecimal specificationVersion = extension.getSpecificationVersion();
        if( null != specificationVersion )
        {
            attributes.putValue( prefix + SPECIFICATION_VERSION,
                                 specificationVersion.toString() );
        }

        final String implementationVendorID = extension.getImplementationVendorID();
        if( null != implementationVendorID )
        {
            attributes.putValue( prefix + IMPLEMENTATION_VENDOR_ID,
                                 implementationVendorID );
        }

        final String implementationVendor = extension.getImplementationVendor();
        if( null != implementationVendor )
        {
            attributes.putValue( prefix + IMPLEMENTATION_VENDOR,
                                 implementationVendor );
        }

        final String implementationVersion = extension.getImplementationVersion();
        if( null != implementationVersion )
        {
            attributes.putValue( prefix + IMPLEMENTATION_VERSION,
                                 implementationVersion.toString() );
        }

        final String implementationURL = extension.getImplementationURL();
        if( null != implementationURL )
        {
            attributes.putValue( prefix + IMPLEMENTATION_URL,
                                 implementationURL );
        }
    }

    /**
     * The constructor to create Extension object.
     * Note that every component is allowed to be specified
     * but only the extensionName is mandatory.
     *
     * @param extensionName the name of extension.
     * @param specificationVersion the specification Version of extension.
     * @param specificationVendor the specification Vendor of extension.
     * @param implementationVersion the implementation Version of extension.
     * @param implementationVendor the implementation Vendor of extension.
     * @param implementationVendorId the implementation VendorId of extension.
     * @param implementationURL the implementation URL of extension.
     */
    public Extension( final String extensionName,
                      final String specificationVersion,
                      final String specificationVendor,
                      final String implementationVersion,
                      final String implementationVendor,
                      final String implementationVendorId,
                      final String implementationURL )
    {
        m_extensionName = extensionName;
        m_specificationVendor = specificationVendor;

        if( null != specificationVersion )
        {
            try
            {
                m_specificationVersion = new DeweyDecimal( specificationVersion );
            }
            catch( final NumberFormatException nfe )
            {
                //
                // don't complain because maven is generating illegal 
                // spec values by default - instead we leave it as null
                //

                //final String error = 
                //    "Bad specification version format '" + specificationVersion +
                //    "' in '" + extensionName + "'. (Reason: " + nfe + ")";
                //throw new IllegalArgumentException( error );
            }
        }

        m_implementationURL = implementationURL;
        m_implementationVendor = implementationVendor;
        m_implementationVendorID = implementationVendorId;
        m_implementationVersion = implementationVersion;

        if( null == m_extensionName )
        {
            throw new NullPointerException( "extensionName property is null" );
        }
    }

    /**
     * Get the name of the extension.
     *
     * @return the name of the extension
     */
    public String getExtensionName()
    {
        return m_extensionName;
    }

    /**
     * Get the vendor of the extensions specification.
     *
     * @return the vendor of the extensions specification.
     */
    public String getSpecificationVendor()
    {
        return m_specificationVendor;
    }

    /**
     * Get the version of the extensions specification.
     *
     * @return the version of the extensions specification.
     */
    public DeweyDecimal getSpecificationVersion()
    {
        return m_specificationVersion;
    }

    /**
     * Get the url of the extensions implementation.
     *
     * @return the url of the extensions implementation.
     */
    public String getImplementationURL()
    {
        return m_implementationURL;
    }

    /**
     * Get the vendor of the extensions implementation.
     *
     * @return the vendor of the extensions implementation.
     */
    public String getImplementationVendor()
    {
        return m_implementationVendor;
    }

    /**
     * Get the vendorID of the extensions implementation.
     *
     * @return the vendorID of the extensions implementation.
     */
    public String getImplementationVendorID()
    {
        return m_implementationVendorID;
    }

    /**
     * Get the version of the extensions implementation.
     *
     * @return the version of the extensions implementation.
     */
    public String getImplementationVersion()
    {
        return m_implementationVersion;
    }

    /**
     * Return a Compatibility enum indicating the relationship of this
     * <code>Extension</code> with the specified <code>Extension</code>.
     *
     * @param required Description of the required optional package
     * @return the enum indicating the compatability (or lack thereof)
     *         of specifed extension
     */
    public Compatability getCompatibilityWith( final Extension required )
    {
        // Extension Name must match
        if( !m_extensionName.equals( required.getExtensionName() ) )
        {
            return INCOMPATIBLE;
        }

        // Available specification version must be >= required
        final DeweyDecimal specificationVersion = required.getSpecificationVersion();
        if( null != specificationVersion )
        {
            if( null == m_specificationVersion ||
                !isCompatible( m_specificationVersion, specificationVersion ) )
            {
                return REQUIRE_SPECIFICATION_UPGRADE;
            }
        }

        // Implementation Vendor ID must match
        final String implementationVendorId = required.getImplementationVendorID();
        if( null != implementationVendorId )
        {
            if( null == m_implementationVendorID ||
                !m_implementationVendorID.equals( implementationVendorId ) )
            {
                return REQUIRE_VENDOR_SWITCH;
            }
        }


        // This available optional package satisfies the requirements
        return COMPATIBLE;
    }

    /**
     * Return <code>true</code> if the specified <code>Extension</code>
     * (which represents an optional package required by an application)
     * is satisfied by this <code>Extension</code> (which represents an
     * optional package that is already installed.  Otherwise, return
     * <code>false</code>.
     *
     * @param required Description of the required optional package
     * @return true if the specified extension is compatible with this extension
     */
    public boolean isCompatibleWith( final Extension required )
    {
        return ( COMPATIBLE == getCompatibilityWith( required ) );
    }

    /**
     * Return a String representation of this object.
     *
     * @return string representation of object.
     */
    public String toString()
    {
        final String lineSeparator = System.getProperty( "line.separator" );
        final String brace = ": ";

        final StringBuffer sb = new StringBuffer( EXTENSION_NAME.toString() );
        sb.append( brace );
        sb.append( m_extensionName );
        sb.append( lineSeparator );

        if( null != m_specificationVersion )
        {
            sb.append( SPECIFICATION_VERSION );
            sb.append( brace );
            sb.append( m_specificationVersion );
            sb.append( lineSeparator );
        }

        if( null != m_specificationVendor )
        {
            sb.append( SPECIFICATION_VENDOR );
            sb.append( brace );
            sb.append( m_specificationVendor );
            sb.append( lineSeparator );
        }

        if( null != m_implementationVersion )
        {
            sb.append( IMPLEMENTATION_VERSION );
            sb.append( brace );
            sb.append( m_implementationVersion );
            sb.append( lineSeparator );
        }

        if( null != m_implementationVendorID )
        {
            sb.append( IMPLEMENTATION_VENDOR_ID );
            sb.append( brace );
            sb.append( m_implementationVendorID );
            sb.append( lineSeparator );
        }

        if( null != m_implementationVendor )
        {
            sb.append( IMPLEMENTATION_VENDOR );
            sb.append( brace );
            sb.append( m_implementationVendor );
            sb.append( lineSeparator );
        }

        if( null != m_implementationURL )
        {
            sb.append( IMPLEMENTATION_URL );
            sb.append( brace );
            sb.append( m_implementationURL );
            sb.append( lineSeparator );
        }

        return sb.toString();
    }

    /**
     * Return <code>true</code> if the first version number is greater than
     * or equal to the second; otherwise return <code>false</code>.
     *
     * @param first First version number (dotted decimal)
     * @param second Second version number (dotted decimal)
     */
    private boolean isCompatible( final DeweyDecimal first, final DeweyDecimal second )
    {
        return first.isGreaterThanOrEqual( second );
    }

    /**
     * Retrieve all the extensions listed under a particular key
     * (Usually EXTENSION_LIST or OPTIONAL_EXTENSION_LIST).
     *
     * @param manifest the manifest to extract extensions from
     * @param listKey the key used to get list (Usually
     *        EXTENSION_LIST or OPTIONAL_EXTENSION_LIST)
     * @return the list of listed extensions
     */
    private static Extension[] getListed( final Manifest manifest,
                                          final Attributes.Name listKey )
    {
        if( null == manifest )
          return new Extension[0];
 
        final ArrayList results = new ArrayList();
        final Attributes mainAttributes = manifest.getMainAttributes();

        if( null != mainAttributes )
        {
            getExtension( mainAttributes, results, listKey );
        }

        final Map entries = manifest.getEntries();
        final Iterator keys = entries.keySet().iterator();
        while( keys.hasNext() )
        {
            final String key = (String)keys.next();
            final Attributes attributes = (Attributes)entries.get( key );
            getExtension( attributes, results, listKey );
        }

        return (Extension[])results.toArray( new Extension[ 0 ] );
    }

    /**
     * Add required optional packages defined in the specified attributes entry, if any.
     *
     * @param attributes Attributes to be parsed
     * @param required list to add required optional packages to
     * @param listKey the key to use to lookup list, usually EXTENSION_LIST
     *    or OPTIONAL_EXTENSION_LIST
     */
    private static void getExtension( final Attributes attributes,
                                      final ArrayList required,
                                      final Attributes.Name listKey )
    {
        final String names = attributes.getValue( listKey );
        if( null == names )
        {
            return;
        }

        final String[] extentions = split( names, " " );
        for( int i = 0; i < extentions.length; i++ )
        {
            final String prefix = extentions[ i ] + "-";
            final Extension extension = getExtension( prefix, attributes );

            if( null != extension )
            {
                required.add( extension );
            }
        }
    }

    /**
     * Splits the string on every token into an array of strings.
     *
     * @param string the string
     * @param onToken the token
     * @return the resultant array
     */
    private static final String[] split( final String string, final String onToken )
    {
        final StringTokenizer tokenizer = new StringTokenizer( string, onToken );
        final String[] result = new String[ tokenizer.countTokens() ];

        for( int i = 0; i < result.length; i++ )
        {
            result[ i ] = tokenizer.nextToken();
        }

        return result;
    }

    /**
     * Extract an Extension from Attributes.
     * Prefix indicates the prefix checked for each string.
     * Usually the prefix is <em>"&lt;extension&gt;-"</em> if looking for a
     * <b>Required</b> extension. If you are looking for an <b>Available</b> extension
     * then the prefix is <em>""</em>.
     *
     * @param prefix the prefix for each attribute name
     * @param attributes Attributes to searched
     * @return the new Extension object, or null
     */
    private static Extension getExtension( final String prefix, final Attributes attributes )
    {
        //WARNING: We trim the values of all the attributes because
        //Some extension declarations are badly defined (ie have spaces
        //after version or vendorID)
        final String nameKey = prefix + EXTENSION_NAME;
        final String name = getTrimmedString( attributes.getValue( nameKey ) );
        if( null == name )
        {
            return null;
        }

        final String specVendorKey = prefix + SPECIFICATION_VENDOR;
        final String specVendor = getTrimmedString( attributes.getValue( specVendorKey ) );
        final String specVersionKey = prefix + SPECIFICATION_VERSION;
        final String specVersion = getTrimmedString( attributes.getValue( specVersionKey ) );

        final String impVersionKey = prefix + IMPLEMENTATION_VERSION;
        final String impVersion = getTrimmedString( attributes.getValue( impVersionKey ) );
        final String impVendorKey = prefix + IMPLEMENTATION_VENDOR;
        final String impVendor = getTrimmedString( attributes.getValue( impVendorKey ) );
        final String impVendorIDKey = prefix + IMPLEMENTATION_VENDOR_ID;
        final String impVendorId = getTrimmedString( attributes.getValue( impVendorIDKey ) );
        final String impURLKey = prefix + IMPLEMENTATION_URL;
        final String impURL = getTrimmedString( attributes.getValue( impURLKey ) );

        return new Extension( name, specVersion, specVendor, impVersion,
                              impVendor, impVendorId, impURL );
    }

    /**
     * Trim the supplied string if the string is non-null
     *
     * @param value the string to trim or null
     * @return the trimmed string or null
     */
    private static String getTrimmedString( final String value )
    {
        if( null == value )
        {
            return null;
        }
        else
        {
            return value.replace('"', ' ').trim();
        }
    }
}
