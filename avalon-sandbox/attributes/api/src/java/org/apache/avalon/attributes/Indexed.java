package org.apache.avalon.attributes;

/**
 * This attribute is used to mark attributes as being indexed.
 * Indexed attributes will result in the attribute-jar-index tool
 * creating an entry for a class marked with an attribute that is indexed.
 * For example, if the attribute Service is Indexed, and the classes
 * MyService and MyService2 have Service as a class attribute, the 
 * jar-index will be:
 *
 * AttributeType: Service
 * Class: MyService
 * Class: MyService2
 */
public class Indexed {
}