// Copyright 2003-2004 The Apache Software Foundation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.namespace Apache.Avalon.Framework{	using System;
	/// <summary>	///	The Logger sending everything to the standard output streams.	/// This is mainly for the cases when you have a utility that	/// does not have a logger to supply.	/// </summary>	public class ConsoleLogger : ILogger	{		/// <summary>
		/// Default logger level
		/// </summary>		private LoggerLevel m_logLevel = LoggerLevel.Debug;		/// <summary>
		/// Default name
		/// </summary>		private String m_name = String.Empty;		/// <summary>		/// Creates a new ConsoleLogger with the priority set to DEBUG.		/// </summary>		public ConsoleLogger(): this(LoggerLevel.Debug)		{		}		/// <summary>		/// Creates a new ConsoleLogger.		/// </summary>		/// <param name="logLevel">The Log level typecode.</param>		public ConsoleLogger(LoggerLevel logLevel)		{			this.m_logLevel = logLevel;		}		/// <summary>		/// Creates a new ConsoleLogger.		/// </summary>		/// <param name="name">The Log name.</param>		public ConsoleLogger(String name)		{			this.m_name = name;		}		/// <summary>		/// Creates a new ConsoleLogger.		/// </summary>		/// <param name="name">The Log name.</param>		/// <param name="logLevel">The Log level typecode.</param>		public ConsoleLogger(String name, LoggerLevel logLevel) : this(name)		{			this.m_logLevel = logLevel;		}		/// <summary>		/// Logs a debug message.		/// </summary>		/// <param name="message">The Message</param>		public void Debug(string message)		{			Debug(message, null as Exception);		}		/// <summary>		/// Logs a debug message. 		/// </summary>		/// <param name="message">The Message</param>		/// <param name="exception">The Exception</param>		public void Debug(string message, Exception exception)		{			Log(LoggerLevel.Debug, message, exception); 		}		/// <summary>		/// Logs a debug message.		/// </summary>		/// <param name="format">Message format</param>		/// <param name="args">Array of objects to write using format</param>		public void Debug( string format, params Object[] args )		{			Debug(String.Format(format, args));		}		/// <summary>		/// Determines if messages of priority "debug" will be logged.		/// </summary>		/// <value>True if "debug" messages will be logged.</value> 		public bool IsDebugEnabled		{			get			{				return (m_logLevel <= LoggerLevel.Debug);			}		}		/// <summary>		/// Logs an info message.		/// </summary>		/// <param name="message">The Message</param>		public void Info( string message )		{			Info(message, null as Exception);		}		/// <summary>		/// Logs an info message. 		/// </summary>		/// <param name="message">The Message</param>		/// <param name="exception">The Exception</param>		public void Info( string message, Exception exception)		{			Log(LoggerLevel.Info, message, exception); 		}		/// <summary>		/// Logs an info message.		/// </summary>		/// <param name="format">Message format</param>		/// <param name="args">Array of objects to write using format</param>		public void Info( string format, params Object[] args )		{			Info(String.Format(format, args));		}		/// <summary>		/// Determines if messages of priority "info" will be logged.		/// </summary>		/// <value>True if "info" messages will be logged.</value>		public bool IsInfoEnabled		{			get			{				return (m_logLevel <= LoggerLevel.Info);			}		}		/// <summary>		/// Logs a warn message.		/// </summary>		/// <param name="message">The Message</param>		public void Warn(string message )		{			Warn(message, null as Exception);		}		/// <summary>		/// Logs a warn message. 		/// </summary>		/// <param name="message">The Message</param>		/// <param name="exception">The Exception</param>		public void Warn(string message, Exception exception)		{			Log(LoggerLevel.Warn, message, exception); 		}		/// <summary>		/// Logs an warn message.		/// </summary>		/// <param name="format">Message format</param>		/// <param name="args">Array of objects to write using format</param>		public void Warn( string format, params Object[] args )		{			Warn(String.Format(format, args));		}		/// <summary>		/// Determines if messages of priority "warn" will be logged.		/// </summary>		/// <value>True if "warn" messages will be logged.</value>		public bool IsWarnEnabled		{			get			{				return (m_logLevel <= LoggerLevel.Warn);			}		}		/// <summary>		/// Logs an error message.		/// </summary>		/// <param name="message">The Message</param>		public void Error(string message )		{			Error(message, null as Exception);		}		/// <summary>		/// Logs an error message. 		/// </summary>		/// <param name="message">The Message</param>		/// <param name="exception">The Exception</param>		public void Error(string message, Exception exception)		{			Log(LoggerLevel.Error, message, exception); 		}
		/// <summary>		/// Logs an error message.		/// </summary>		/// <param name="format">Message format</param>		/// <param name="args">Array of objects to write using format</param>		public void Error( string format, params Object[] args )		{			Error(String.Format(format, args));		}		/// <summary>		/// Determines if messages of priority "error" will be logged.		/// </summary>		/// <value>True if "error" messages will be logged.</value>		public bool IsErrorEnabled		{			get			{				return (m_logLevel <= LoggerLevel.Error);			}		}		/// <summary>		/// Logs a fatal error message.		/// </summary>		/// <param name="message">The Message</param>		public void FatalError(string message )		{			FatalError(message, null as Exception);		}		/// <summary>		/// Logs a fatal error message.		/// </summary>		/// <param name="message">The Message</param>		/// <param name="exception">The Exception</param>		public void FatalError(string message, Exception exception)		{			Log(LoggerLevel.Fatal, message, exception); 		}		/// <summary>		/// Logs a fatal error message.		/// </summary>		/// <param name="format">Message format</param>		/// <param name="args">Array of objects to write using format</param>		public void FatalError( string format, params Object[] args )		{			FatalError(String.Format(format, args));		}		/// <summary>		/// Determines if messages of priority "fatalError" will be logged.		/// </summary>		/// <value>True if "fatalError" messages will be logged.</value>		public bool IsFatalErrorEnabled		{			get 			{				return (m_logLevel <= LoggerLevel.Fatal); 			}		}		/// <summary>		/// A Common method to log.		/// </summary>		/// <param name="level">The level of logging</param>		/// <param name="levelName">The Level name</param>		/// <param name="message">The Message</param>		/// <param name="exception">The Exception</param>		protected void Log(LoggerLevel level, string message, Exception exception) 		{			if(m_logLevel <= level)			{				Console.Out.WriteLine(string.Format("[{0}] '{1}' {2}", level.ToString(), m_name, message));								if(exception != null)				{					Console.Out.WriteLine(exception.StackTrace);				}			}		}		/// <summary>		///	Just returns this logger (<c>ConsoleLogger</c> is not hierarchical).		/// </summary>		/// <param name="name">Ignored</param>		/// <returns>This ILogger instance.</returns> 		public ILogger CreateChildLogger(string name )		{			return new ConsoleLogger( String.Format("{0}.{1}", this.m_name, name), m_logLevel );		}	}}