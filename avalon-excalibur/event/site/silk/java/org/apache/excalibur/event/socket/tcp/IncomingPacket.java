/* 
 * Copyright (c) 2000 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.excalibur.event.socket.tcp;

import org.apache.excalibur.event.socket.Buffer;

/** 
 * An IncomingPacket represents a packet which was 
 * received from an asynchronous socket. When a packet 
 * is received on a connection, a IncomingPacket is 
 * pushed to the Sink associated with the AsyncTcpConnection.
 *
 * @version $Revision: 1.1 $
 * @author  <a href="mailto:schierma@users.sourceforge.net">schierma</a>
 */
public class IncomingPacket
{
  /** The connection that received this packet. */
  private final AsyncTcpConnection m_connection;

  /** The buffer element representing the data of the packet */
  private final Buffer m_bufferElement;

  /** The sequence index number of the packet. */
  private final long m_index;

  //----------------------- IncomingPacket constructors
  /**
   * Creates a new IncomingPacket based on the passed in
   * connection and buffer element.  The packet has an index
   * of <m_code>0</m_code>. 
   * @since May 21, 2002)
   * 
   * @param connection
   *  The TCP connection for this packet
   * @param bufferElement 
   *  The bufferElement that represents the packets data
   */
  public IncomingPacket(AsyncTcpConnection connection, Buffer bufferElement)
  {
    this(connection, bufferElement, 0);
  }


  /**
   * Creates a new IncomingPacket based on the passed in
   * connection and buffer element and index. 
   * @since May 21, 2002)
   * 
   * @param connection
   *  The TCP connection for this packet
   * @param bufferElement 
   *  The bufferElement that represents the packets data
   * @param index
   *  The sequence number of the packet in the stream.
   */
  public IncomingPacket(
    AsyncTcpConnection connection,
    Buffer bufferElement,
    long index)
  {
    this.m_connection = connection;
    this.m_bufferElement = bufferElement;
    this.m_index = index;
  }

  /**
   * Creates a new IncomingPacket based on the passed in
   * connection, data byte array, read length. The packet 
   * has an index of <m_code>0</m_code> and the data is copied.
   * @since May 21, 2002)
   * 
   * @param connection
   *  The TCP connection for this packet
   * @param data 
   *  The byte array that represents the packets data
   * @param length
   *  The amount of bytes to read from the array
   */
  public IncomingPacket(AsyncTcpConnection connection, byte data[], int length)
  {
    this(connection, data, length, true);
  }

  /**
   * Creates a new IncomingPacket based on the passed in
   * connection, data byte array, read length, and index.
   * The byte array data is copied before being used.
   * @since May 21, 2002)
   * 
   * @param connection
   *  The TCP connection for this packet
   * @param data 
   *  The byte array that represents the packets data
   * @param length
   *  The amount of bytes to read from the array
   * @param index
   *  The sequence number of the packet in the stream.
   */
  public IncomingPacket(
    AsyncTcpConnection connection,
    byte data[],
    int length,
    long index)
  {
    this(connection, data, length, true, index);
  }

  /**
   * Creates a new IncomingPacket based on the passed in
   * connection, data byte array, read length, and an index
   * of <m_code>0</m_code>. The copy flag indicates if the array 
   * should be copied before used.
   * @since May 21, 2002)
   * 
   * @param connection
   *  The TCP connection for this packet
   * @param data 
   *  The byte array that represents the packets data
   * @param length
   *  The amount of bytes to read from the array
   * @param copy
   *  <m_code>true</m_code> if the array should be copied before
   *  being used.
   */
  public IncomingPacket(
    AsyncTcpConnection connection,
    byte[] data,
    int length,
    boolean copy)
  {
    this(connection, data, length, copy, 0);
  }

  /**
   * Creates a new IncomingPacket based on the passed in
   * connection, data byte array, read length, and index.
   * The copy flag indicates if the array should be copied
   * before used.
   * @since May 21, 2002)
   * 
   * @param connection
   *  The TCP connection for this packet
   * @param data 
   *  The byte array that represents the packets data
   * @param length
   *  The amount of bytes to read from the array
   * @param copy
   *  <m_code>true</m_code> if the array should be copied before
   *  being used.
   * @param index
   *  The sequence number of the packet in the stream.
   */
  public IncomingPacket(
    AsyncTcpConnection connection,
    byte[] data,
    int length,
    boolean copy,
    long index)
  {
    if (copy)
    {
      final byte newdata[] = new byte[length];
      System.arraycopy(data, 0, newdata, 0, length);
      m_bufferElement = new Buffer(newdata);
    }
    else
    {
      m_bufferElement = new Buffer(data, 0, length);
    }

    m_connection = connection;
    m_index = index;
  }

  //---------------------- IncomingPacket specific implementation
  /**
   * Returns the connection from which this packet 
   * was received.
   * @since May 21, 2002)
   * 
   * @return {@link AsyncConnection}
   *  the connection from which this packet was received.
   */
  public AsyncTcpConnection getConnection()
  {
    return m_connection;
  }

  /** 
   * Returns the data from an incoming TCP packet.
   * @since May 21, 2002)
   * 
   * @return byte[]
   *  the data from an incoming TCP packet.
   */
  public byte[] getBytes()
  {
    return m_bufferElement.getData();
  }

  /**
   * Returns the size of the packet data.
   * @since May 21, 2002)
   *  
   * @return int
   *  the size of the data package
   */
  public int size()
  {
    return m_bufferElement.getSize();
  }

  /**
   * Returns the sequence number associated with this packet.
   * Sequence numbers range from 1 to Long.MAX_VALUE, then 
   * wrap around to Long.MIN_VALUE. A sequence number of 0 
   * indicates that no sequence number was associated with 
   * this packet when it was created.
   * @since May 21, 2002)
   * 
   * @return long
   *  the sequence number associated with this packet.
   */
  public long getIndex()
  {
    return m_index;
  }
}