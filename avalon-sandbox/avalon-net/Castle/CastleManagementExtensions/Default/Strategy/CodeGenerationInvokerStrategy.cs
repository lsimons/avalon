// ============================================================================
//                   The Apache Software License, Version 1.1
// ============================================================================
// 
// Copyright (C) 2002-2003 The Apache Software Foundation. All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modifica-
// tion, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of  source code must  retain the above copyright  notice,
//    this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 
// 3. The end-user documentation included with the redistribution, if any, must
//    include  the following  acknowledgment:  "This product includes  software
//    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
//    Alternately, this  acknowledgment may  appear in the software itself,  if
//    and wherever such third-party acknowledgments normally appear.
// 
// 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
//    must not be used to endorse or promote products derived from this  software 
//    without  prior written permission. For written permission, please contact 
//    apache@apache.org.
// 
// 5. Products  derived from this software may not  be called "Apache", nor may
//    "Apache" appear  in their name,  without prior written permission  of the
//    Apache Software Foundation.
// 
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
// APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
// INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
// DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
// ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
// (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// 
// This software  consists of voluntary contributions made  by many individuals
// on  behalf of the Apache Software  Foundation. For more  information on the 
// Apache Software Foundation, please see <http://www.apache.org/>.
// ============================================================================

namespace Apache.Avalon.Castle.ManagementExtensions.Default.Strategy
{
	using System;
	using System.IO;
	using System.Reflection;
	using System.CodeDom;
	using System.CodeDom.Compiler;

	/// <summary>
	/// Summary description for CodeGenerationInvokerStrategy.
	/// </summary>
	public sealed class CodeGenerationInvokerStrategy : InvokerStrategy
	{
		public CodeGenerationInvokerStrategy()
		{
		}

		#region InvokerStrategy Members

		public MDynamicSupport Create(Object instance)
		{
			ManagementInfo info = MInspector.BuildInfoFromStandardComponent(instance);

			CodeGenerationDynamicSupport codeGen = new CodeGenerationDynamicSupport(
				instance, info, 
				new MemberResolver(info, instance.GetType()));

			return codeGen.GetImplementation();
		}

		#endregion
	}

	/// <summary>
	/// 
	/// </summary>
	class CodeGenerationDynamicSupport
	{
		private Object instance;
		private ManagementInfo info;
		private MemberResolver resolver;

		public CodeGenerationDynamicSupport(Object instance, ManagementInfo info, MemberResolver resolver)
		{
			this.info     = info;
			this.instance = instance;
			this.resolver = resolver;
		}

		public MDynamicSupport GetImplementation()
		{
			// TODO: Adds a cache for generated assemblies...

			CodeNamespace ns = BuildMDynamicSupportCodeDom();

			Microsoft.CSharp.CSharpCodeProvider cp = new Microsoft.CSharp.CSharpCodeProvider();

			cp.CreateGenerator().GenerateCodeFromNamespace( ns, Console.Out, null );

			CodeCompileUnit unit = new CodeCompileUnit();
			unit.Namespaces.Add( ns );

			String[] assembliesName = GetRequiredAssemblies(); 

			CompilerParameters parameters = new CompilerParameters(assembliesName);
			parameters.GenerateExecutable = false;
			parameters.GenerateInMemory   = true;

			CompilerResults result = 
				cp.CreateCompiler().CompileAssemblyFromDom(parameters, unit);

			if (result.Errors.HasErrors)
			{
				throw new CompilationException(result.Errors);
			}

			Type dynamicType = 
				result.CompiledAssembly.GetType(
					"Apache.Avalon.Castle.ManagementExtensions.Generated.DynamicImplementation", true, true);

			Object inst = Activator.CreateInstance(
				dynamicType, 
				new object[] { info, instance } );

			return (MDynamicSupport) inst;
		}

		private String[] GetRequiredAssemblies()
		{
			return new String[] { Path.GetFileName( Assembly.GetExecutingAssembly().CodeBase ),
								  Path.GetFileName( instance.GetType().Assembly.CodeBase ) } ;
		}

		private CodeNamespace BuildMDynamicSupportCodeDom()
		{
			CodeNamespace ns = new CodeNamespace( "Apache.Avalon.Castle.ManagementExtensions.Generated" );
			ns.Imports.Add( new CodeNamespaceImport("Apache.Avalon.Castle.ManagementExtensions") );

			CodeTypeDeclaration typeDefinition = new CodeTypeDeclaration("DynamicImplementation");
			ns.Types.Add(typeDefinition);

			typeDefinition.IsClass = true;
			typeDefinition.BaseTypes.Add( typeof(MDynamicSupport) );

			BuildFields(typeDefinition);
			BuildConstructor(typeDefinition);
			BuildMethods(typeDefinition);

			return ns;
		}

		private void BuildFields(CodeTypeDeclaration typeDefinition)
		{
			CodeMemberField fieldInfo = new CodeMemberField( typeof(ManagementInfo), "info" );
			CodeMemberField fieldInstance = new CodeMemberField( instance.GetType(), "instance" );

			typeDefinition.Members.Add(fieldInfo);
			typeDefinition.Members.Add(fieldInstance);
		}

		private void BuildConstructor(CodeTypeDeclaration typeDefinition)
		{
			CodeConstructor constructor = new CodeConstructor();
			
			constructor.Parameters.Add(
				new CodeParameterDeclarationExpression(typeof(ManagementInfo), "info"));
			constructor.Parameters.Add(
				new CodeParameterDeclarationExpression(instance.GetType(), "instance"));
			
			constructor.Statements.Add(
				new CodeAssignStatement(
				new CodeFieldReferenceExpression(
				new CodeThisReferenceExpression(), "info"), 
				new CodeArgumentReferenceExpression("info")));

			constructor.Statements.Add(
				new CodeAssignStatement(
				new CodeFieldReferenceExpression(
				new CodeThisReferenceExpression(), "instance"), 
				new CodeArgumentReferenceExpression("instance")));

			constructor.Attributes = MemberAttributes.Public;

			typeDefinition.Members.Add( constructor );
		}

		private void BuildMethods(CodeTypeDeclaration typeDefinition)
		{
			BuildInvokeMethod(typeDefinition);
			BuildInfoProperty(typeDefinition);
			BuildGetAttributeMethod(typeDefinition);
			BuildSetAttributeMethod(typeDefinition);
		}

		private void BuildInfoProperty(CodeTypeDeclaration typeDefinition)
		{
			CodeMemberProperty infoProperty = new CodeMemberProperty();

			infoProperty.HasGet = true;
			infoProperty.HasSet = false;
			infoProperty.Type = new CodeTypeReference( typeof(ManagementInfo) );
			infoProperty.Name = "Info";
			infoProperty.Attributes = MemberAttributes.Public;

			CodeMethodReturnStatement returnStmt = new CodeMethodReturnStatement();
			returnStmt.Expression = new CodeFieldReferenceExpression(
				new CodeThisReferenceExpression(), "info");

			infoProperty.GetStatements.Add(returnStmt);

			typeDefinition.Members.Add(infoProperty);
		}

		private void BuildGetAttributeMethod(CodeTypeDeclaration typeDefinition)
		{
			CodeMemberMethod method = new CodeMemberMethod();

			method.Attributes = MemberAttributes.Public;
			method.Name = "GetAttributeValue";
			method.ReturnType = new CodeTypeReference( typeof(Object) );
			method.Parameters.Add(new CodeParameterDeclarationExpression( 
				typeof(String), "name" ));

			CodeArgumentReferenceExpression nameArgument = 
				new CodeArgumentReferenceExpression("name");
			CodeThisReferenceExpression thisRef = 
				new CodeThisReferenceExpression();

			foreach(ManagementAttribute attribute in info.Attributes)
			{
				CodeConditionStatement stmt = new CodeConditionStatement();
				stmt.Condition = 
					new CodeMethodInvokeExpression(nameArgument, "Equals", 
					new CodeSnippetExpression("\"" + attribute.Name + "\""));

				PropertyInfo property = resolver.GetProperty(attribute.Name);

				if (!property.CanRead)
				{
					// TODO: Adds throw
					
					continue;
				}

				CodeMethodReturnStatement returnStmt = new CodeMethodReturnStatement();
				returnStmt.Expression = 
					new CodePropertyReferenceExpression(
						new CodeFieldReferenceExpression(
							thisRef, "instance"), attribute.Name );

				stmt.TrueStatements.Add( returnStmt );

				method.Statements.Add(stmt);
			}

			{
				CodeMethodReturnStatement returnStmt = new CodeMethodReturnStatement();
				returnStmt.Expression = new CodeSnippetExpression("null");
				method.Statements.Add( returnStmt );
			}

			typeDefinition.Members.Add( method );
		}

		private void BuildSetAttributeMethod(CodeTypeDeclaration typeDefinition)
		{
			CodeMemberMethod method = new CodeMemberMethod();
			
			method.Attributes = MemberAttributes.Public;
			method.Name = "SetAttributeValue";
			method.Parameters.Add(new CodeParameterDeclarationExpression( 
				typeof(String), "name" ));
			method.Parameters.Add(new CodeParameterDeclarationExpression( 
				typeof(Object), "value" ));

			CodeArgumentReferenceExpression nameArgument  = 
				new CodeArgumentReferenceExpression("name");
			CodeArgumentReferenceExpression valueArgument = 
				new CodeArgumentReferenceExpression("value");
			CodeThisReferenceExpression thisRef = 
				new CodeThisReferenceExpression();

			foreach(ManagementAttribute attribute in info.Attributes)
			{
				CodeConditionStatement stmt = new CodeConditionStatement();
				stmt.Condition = 
					new CodeMethodInvokeExpression(nameArgument, "Equals", 
					new CodeSnippetExpression("\"" + attribute.Name + "\""));

				PropertyInfo property = resolver.GetProperty(attribute.Name);

				if (!property.CanWrite)
				{
					// TODO: Adds throw
					
					continue;
				}

				CodeAssignStatement assignStatement = new CodeAssignStatement();
				assignStatement.Left = 
					new CodePropertyReferenceExpression( 
						new CodeFieldReferenceExpression(thisRef, "instance"), attribute.Name );
				assignStatement.Right = 
					new CodeCastExpression( property.PropertyType, 
						new CodeArgumentReferenceExpression("value") );

				stmt.TrueStatements.Add( assignStatement );

				method.Statements.Add(stmt);
			}

			typeDefinition.Members.Add( method );
		}

		private void BuildInvokeMethod(CodeTypeDeclaration typeDefinition)
		{
			CodeMemberMethod method = new CodeMemberMethod();

			method.Attributes = MemberAttributes.Public;
			method.Name = "Invoke";
			method.ReturnType = new CodeTypeReference( typeof(Object) );
			method.Parameters.Add(new CodeParameterDeclarationExpression( 
				typeof(String), "action" ));
			method.Parameters.Add(new CodeParameterDeclarationExpression( 
				typeof(Object[]), "args" ));
			method.Parameters.Add(new CodeParameterDeclarationExpression( 
				typeof(Type[]), "sign" ));

			// TODO: Statement to check if action is null 

			CodeArgumentReferenceExpression actionArgument = 
				new CodeArgumentReferenceExpression("action");
			CodeThisReferenceExpression thisRef = 
				new CodeThisReferenceExpression();

			foreach(MethodInfo methodInfo in resolver.Methods)
			{
				CodeConditionStatement stmt = new CodeConditionStatement();
				stmt.Condition = 
					new CodeMethodInvokeExpression(actionArgument, "Equals", 
						new CodeSnippetExpression("\"" + methodInfo.Name + "\""));

				CodeMethodInvokeExpression methodInv = 
					new CodeMethodInvokeExpression(
						new CodeFieldReferenceExpression(thisRef, "instance"), methodInfo.Name);
				
				int index = 0;
				foreach(ParameterInfo parameter in methodInfo.GetParameters())
				{
					CodeVariableReferenceExpression varExp = 
						new CodeVariableReferenceExpression("args[" + index++ + "]");
					CodeCastExpression castExp = new CodeCastExpression(
						parameter.ParameterType, 
						varExp);
					
					methodInv.Parameters.Add(castExp);
				}

				stmt.TrueStatements.Add(methodInv);

				method.Statements.Add(stmt);
			}

			CodeMethodReturnStatement returnStmt = new CodeMethodReturnStatement();
			returnStmt.Expression = new CodeSnippetExpression("null");
			method.Statements.Add(returnStmt);

			typeDefinition.Members.Add( method );
		}
	}
}
