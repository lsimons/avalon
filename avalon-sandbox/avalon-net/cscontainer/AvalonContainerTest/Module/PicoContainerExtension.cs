namespace Apache.Avalon.Container.Test.Module
{
	using System;
	using System.Reflection;

	using Apache.Avalon.Container.Extension;

	/// <summary>
	/// Summary description for PicoContainerExtension.
	/// </summary>
	public class PicoContainerExtension : IExtensionModule
	{
		public PicoContainerExtension()
		{
		}

		#region IExtensionModule Members

		public void Init(LifecycleManager manager)
		{
			manager.SetupDependencies += new LifecycleHandler(OnSetupDependencies);
		}

		#endregion

		private void OnSetupDependencies(LifecycleEventArgs e)
		{
			object instance = e.Component;

			MethodInfo method = instance.GetType().GetMethod("set_" + e.DependencyKey, 
				BindingFlags.SetProperty|BindingFlags.Public|BindingFlags.Instance);

			if (method != null)
			{
				method.Invoke(instance, new object[] { e.DependencyInstance } );
			}
		}
	}
}
