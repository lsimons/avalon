// Copyright 2004 Apache Software Foundation
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
// limitations under the License.

namespace Apache.Avalon.Composition.Data
{
	using System;
	
	using Apache.Avalon.Framework;
	using Apache.Avalon.Meta;
	
	/// <summary> Definition of the criteria for an explicit component profile.  A profile, when
	/// included within the scope of a container declaration will be instantiated in
	/// the model as an EXPLICIT component profile resulting in the initiation of
	/// dependency resolution relative to the component as the target deployment
	/// objective.  Multiple supplementary profiles may be packaged in a .xprofiles
	/// resources and will be assigned to the container automatically.  In the absence
	/// of explicit or packaged profile directives, an implicit profile will be created
	/// for any component types declared under a jar manifest.
	/// 
	/// <p><b>XML</b></p>
	/// <p>A component element declares the profile to be applied during the instantiation
	/// of a component type.  It includes a name and class declaration, logging directives
	/// (resolved relative to the component's container), context creation criteria,
	/// together with configuration or parameters information.</p>
	/// 
	/// <pre>
	/// <font color="gray"><i>&lt;!--
	/// Declaration of the services hosted by this container.  Service container here
	/// will be managed relative to other provider components at the same level and
	/// may be serviced by components declared in parent container.
	/// --&gt;</i></font>
	/// &lt;component name="<font color="darkred">complex</font>" class="<font color="darkred">org.apache.excalibur.playground.ComplexComponent</font>" activation="<font color="darkred">startup</font>"&gt;
	/// <font color="gray"><i>&lt;!--
	/// Priority and target assignments for component specific logging categrories.
	/// --&gt;</i></font>
	/// &lt;categories priority="<font color="darkred">DEBUG</font>"&gt;
	/// &lt;category name="<font color="darkred">init</font>" priority="<font color="darkred">DEBUG</font>" /&gt;
	/// &lt;/categories&gt;
	/// <font color="gray"><i>&lt;!--
	/// Context entry directives are normally only required in the case where the component
	/// type declares a required context type and entry values. Generally speaking, a component
	/// will normally qualify it's instantiation criteria through a configuration declaration.
	/// Any context values defined at this level will override context values supplied by the
	/// container.  The following two context directives for "location" and "home" demonstrate
	/// programatics creation of context values.  The first entry declares that the context
	/// value to be assigned to the key "location" shall be the String value "Paris".  The second
	/// context enty assignes the container's context value for "urn:avalon:home" to the component's
	/// context key of "home".
	/// --&gt;</i></font>
	/// &lt;context&gt;
	/// &lt;entry key="<font color="darkred">location</font>"&gt;<font color="darkred">Paris</font>&lt;/entry&gt;
	/// &lt;include name="<font color="darkred">urn:avalon:home</font>" key="<font color="darkred">home</font>"/&gt;
	/// &lt;/context&gt;
	/// <font color="gray"><i>&lt;!--
	/// Apply the following configuration when instantiating the component.  This configuration
	/// will be applied as the primary configuration in a cascading configuration chain.  A
	/// type may declare a default configuration under a "classname".xconfig file that will be
	/// used to dereference any configuration requests not resolvable by the configuration
	/// supplied here.
	/// --&gt;</i></font>
	/// &lt;configuration&gt;
	/// &lt;message value="<font color="darkred">Hello</font>"/&gt;
	/// &lt;/configuration&gt;
	/// <font color="gray"><i>&lt;!--
	/// The parameterization criteria from this instance of the component type.
	/// --&gt;</i></font>
	/// &lt;parameters/&gt;
	/// &lt;/component&gt;
	/// </pre>
	/// 
	/// 
	/// </summary>
	/// <author>  <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
	/// </author>
	/// <version>  $Revision: 1.1 $ $Date: 2004/02/01 13:31:01 $
	/// </version>
	public class ComponentProfile : Profile
	{
		/// <summary> The assigned logging categories.</summary>
		private CategoriesDirective m_categories;
		
		/// <summary> The collection policy override.</summary>
		private CollectionPolicy m_collection;
		
		/// <summary> The component classname.</summary>
		private System.String m_classname;
		
		/// <summary> The parameters for component (if any).</summary>
		// private Parameters m_parameters;
		
		/// <summary> The configuration for component (if any).</summary>
		private IConfiguration m_configuration;
		
		/// <summary> The configuration for component (if any).</summary>
		private ContextDirective m_context;
		
		/// <summary> The dependency directives.</summary>
		private DependencyDirective[] m_dependencies;
		
		/// <summary> The stage directives.</summary>
		private StageDirective[] m_stages;
		
		/// <summary> The creation mode.</summary>
		private Mode m_mode;
		
		//--------------------------------------------------------------------------
		// constructor
		//--------------------------------------------------------------------------
		
		/// <summary> Creation of a new profile using IMPLICT mode and LIBERAL collection
		/// policies.
		/// 
		/// </summary>
		/// <param name="name">the name to assign to the component deployment scenario
		/// </param>
		/// <param name="classname">the classname of the component type
		/// </param>
		public ComponentProfile(System.String name, System.String classname) : this(name, ActivationPolicy.Lazy, CollectionPolicy.Undefined, classname, null, null, null, null, null, Mode.Implicit)
		{
		}
		
		/// <summary> Creation of a new deployment profile using a supplied template profile.</summary>
		/// <param name="name">the name to assign to the created profile
		/// </param>
		/// <param name="template">the template deployment profile
		/// </param>
		public ComponentProfile(System.String name, ComponentProfile template) : 
			this(name, template.ActivationPolicy, 
			template.CollectionPolicy, template.m_classname, 
			template.m_categories, template.m_context, 
			template.m_dependencies, template.m_stages, /*template.m_parameters,*/
			template.m_configuration, Mode.Explicit)
		{
		}
		
		public ComponentProfile(System.String name, ActivationPolicy activation, 
			CollectionPolicy collection, System.String classname, CategoriesDirective categories,
			ContextDirective context, DependencyDirective[] dependencies, 
			StageDirective[] stages, /*Parameters parameters,*/ 
			IConfiguration config, Mode mode) : base(name, activation, mode)
		{
			if (null == (System.Object) classname)
			{
				throw new System.NullReferenceException("classname");
			}
			
			m_mode = mode;
			m_collection = collection;
			m_classname = classname;
			m_categories = categories;
			m_context = context;
			// m_parameters = parameters;
			m_configuration = config;
			
			if (null == dependencies)
			{
				m_dependencies = new DependencyDirective[0];
			}
			else
			{
				m_dependencies = dependencies;
			}
			
			if (null == stages)
			{
				m_stages = new StageDirective[0];
			}
			else
			{
				m_stages = stages;
			}
		}
		
		//--------------------------------------------------------------------------
		// implementation
		//--------------------------------------------------------------------------
		
		/// <summary> Return the dependency directive for a supplied key.
		/// 
		/// </summary>
		/// <returns> the matching DependencyDirective (possibly null if 
		/// no directive is declared for the given key)
		/// </returns>
		public virtual DependencyDirective getDependencyDirective(System.String key)
		{
			DependencyDirective[] directives = DependencyDirectives;
			for (int i = 0; i < directives.Length; i++)
			{
				DependencyDirective directive = directives[i];
				if (directive.Key.Equals(key))
				{
					return directive;
				}
			}
			return null;
		}
		
		/// <summary> Return the dependency directive for a supplied key.
		/// 
		/// </summary>
		/// <returns> the matching DependencyDirective (possibly null if 
		/// no directive is declared for the given key)
		/// </returns>
		public virtual StageDirective getStageDirective(System.String key)
		{
			StageDirective[] directives = StageDirectives;
			for (int i = 0; i < directives.Length; i++)
			{
				StageDirective directive = directives[i];
				if (directive.Key.Equals(key))
				{
					return directive;
				}
			}
			return null;
		}

		/// <summary> Return the component type classname.
		/// 
		/// </summary>
		/// <returns> classname of the component type
		/// </returns>
		public virtual System.String Classname
		{
			get
			{
				return m_classname;
			}
			
		}
		/// <summary> Return the component collection policy.  If null, the component
		/// type collection policy will apply.
		/// 
		/// </summary>
		/// <returns> a HARD, WEAK, SOFT or UNDEFINED
		/// </returns>
		public virtual CollectionPolicy CollectionPolicy
		{
			get
			{
				return m_collection;
			}
			
		}
		/// <summary> Return the logging categories for the profile.
		/// 
		/// </summary>
		/// <returns> the logger
		/// </returns>
		public virtual CategoriesDirective Categories
		{
			get
			{
				return m_categories;
			}
			
		}
		/// <summary> Return the context directive for the profile.
		/// 
		/// </summary>
		/// <returns> the ContextDirective for the profile.
		/// </returns>
		public virtual ContextDirective Context
		{
			get
			{
				return m_context;
			}
			
		}
		/// <summary> Return the dependency directives.
		/// 
		/// </summary>
		/// <returns> the set of DependencyDirective statements for the profile.
		/// </returns>
		public virtual DependencyDirective[] DependencyDirectives
		{
			get
			{
				return m_dependencies;
			}
			
		}
		/// <summary> Return the stage directives.
		/// 
		/// </summary>
		/// <returns> the set of StageDirective statements for the profile.
		/// </returns>
		public virtual StageDirective[] StageDirectives
		{
			get
			{
				return m_stages;
			}
			
		}
		/// <summary> Return the Parameters for the profile.
		/// 
		/// </summary>
		/// <returns> the Parameters for Component (if any).
		/// </returns>
		/* public virtual Parameters Parameters
		{
			get
			{
				return m_parameters;
			}
			
		}*/

		/// <summary> Return the base Configuration for the profile.  The implementation
		/// garantees that the supplied configuration is not null.
		/// 
		/// </summary>
		/// <returns> the base Configuration for profile.
		/// </returns>
		public virtual IConfiguration Configuration
		{
			get
			{
				return m_configuration;
			}
			
		}
		
		/// <summary> Returns a string representation of the profile.</summary>
		/// <returns> a string representation
		/// </returns>
		public override System.String ToString()
		{
			return "[" + Name + "]";
		}
	}
}