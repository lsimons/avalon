/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.phoenix.engine.deployer;

import java.io.File;

import org.apache.avalon.camelot.Deployer;
import org.apache.avalon.camelot.AbstractDeployer;
import org.apache.avalon.camelot.DeploymentException;

/**
 *
 * @author <a href="mailto:mail@leosimons.com">Leo Simons</a>
 */
public class DefaultDeployer extends AbstractDeployer implements Deployer
{
        public void deployFromFile( String location, File file ) throws DeploymentException
        {
        }
}