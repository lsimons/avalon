/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.xml.xslt;

import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceValidity;
import org.xml.sax.XMLFilter;

/**
 * This is the interface of the XSLT processor.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @author <a href="mailto:proyal@apache.org">Peter Royal</a>
 * @version CVS $Id: XSLTProcessor.java,v 1.7 2003/01/22 02:18:17 jefft Exp $
 * @version 1.0
 * @since   July 11, 2001
 */
public interface XSLTProcessor
    extends Component
{
    /**
     * The role implemented by an <code>XSLTProcessor</code>.
     */
    String ROLE = XSLTProcessor.class.getName();

    public static class TransformerHandlerAndValidity
    {
        private final TransformerHandler transformerHandler;
        private final SourceValidity transformerValidity;

        protected TransformerHandlerAndValidity( final TransformerHandler transformerHandler,
                                                 final SourceValidity transformerValidity )
        {
            this.transformerHandler = transformerHandler;
            this.transformerValidity = transformerValidity;
        }

        public TransformerHandler getTransfomerHandler()
        {
            return transformerHandler;
        }

        public SourceValidity getTransfomerValidity()
        {
            return transformerValidity;
        }
    }

    /**
     * Set the TransformerFactory for this instance.
     * The <code>factory</code> is invoked to return a
     * <code>TransformerHandler</code> to perform the transformation.
     *
     * @param classname the name of the class implementing
     * <code>TransformerFactory</code> value. If an error is found
     * or the indicated class doesn't implement the required interface
     * the original factory of the component is maintained.
     */
    void setTransformerFactory( String classname );

    /**
     * <p>Return a <code>TransformerHandler</code> for a given
     * stylesheet {@link Source}. This can be used in a pipeline to
     * handle the transformation of a stream of SAX events. See {@link
     * org.apache.cocoon.transformation.TraxTransformer#setConsumer} for
     * an example of how to use this method.
     *
     * <p>The additional <code>filter</code> argument, if it's not
     * <code>null</code>, is inserted in the chain SAX events as an XML
     * filter during the parsing or the source document.
     *
     * <p>This method caches the Templates object with meta information
     * (modification time and list of included stylesheets) and performs
     * a reparsing only if this changes.
     *
     * @param stylesheet a {@link Source} value
     * @param filter a {@link XMLFilter} value
     * @return a {@link TransformerHandler} value
     * @exception XSLTProcessorException if an error occurs
     */
    TransformerHandler getTransformerHandler( Source stylesheet, XMLFilter filter )
        throws XSLTProcessorException;

    /**
     * <p>Return a {@link TransformerHandler} and
     * <code>SourceValidity</code> for a given stylesheet
     * {@link Source}. This can be used in a pipeline to
     * handle the transformation of a stream of SAX events. See {@link
     * org.apache.cocoon.transformation.TraxTransformer#setConsumer} for
     * an example of how to use this method.
     *
     * <p>The additional <code>filter</code> argument, if it's not
     * <code>null</code>, is inserted in the chain SAX events as an XML
     * filter during the parsing or the source document.
     *
     * <p>This method caches the Templates object with meta information
     * (modification time and list of included stylesheets) and performs
     * a reparsing only if this changes.
     *
     * @param stylesheet a {@link Source} value
     * @param filter a {@link XMLFilter} value
     * @return a <code>TransformerHandlerAndValidity</code> value
     * @exception XSLTProcessorException if an error occurs
     */
    TransformerHandlerAndValidity getTransformerHandlerAndValidity( Source stylesheet, XMLFilter filter )
        throws XSLTProcessorException;

    /**
     * Same as {@link #getTransformerHandler(Source,XMLFilter)}, with
     * <code>filter</code> set to <code>null</code>.
     *
     * @param stylesheet a {@link Source} value
     * @return a {@link TransformerHandler} value
     * @exception XSLTProcessorException if an error occurs
     */
    TransformerHandler getTransformerHandler( Source stylesheet )
        throws XSLTProcessorException;

    /**
     * Same as {@link #getTransformerHandlerAndValidity(Source,XMLFilter)}, with
     * <code>filter</code> set to <code>null</code>.
     *
     * @param stylesheet a {@link Source} value
     * @return a {@link TransformerHandlerAndValidity} value
     * @exception XSLTProcessorException if an error occurs
     */
    TransformerHandlerAndValidity getTransformerHandlerAndValidity( Source stylesheet )
        throws XSLTProcessorException;

    /**
     * Applies an XSLT stylesheet to an XML document. The source and
     * stylesheet documents are specified as {@link Source}
     * objects. The result of the transformation is placed in
     * {@link Result}, which should be properly initialized before
     * invoking this method. Any additional parameters passed in
     * {@link Parameters params} will become arguments to the stylesheet.
     *
     * @param source a {@link Source} value
     * @param stylesheet a {@link Source} value
     * @param params a <code>Parameters</code> value
     * @param result a <code>Result</code> value
     * @exception XSLTProcessorException if an error occurs
     */
    void transform( Source source, Source stylesheet, Parameters params, Result result )
        throws XSLTProcessorException;
}
