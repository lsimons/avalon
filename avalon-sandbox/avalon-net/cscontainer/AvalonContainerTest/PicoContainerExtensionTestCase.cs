namespace Apache.Avalon.Container.Test
{
	using System;
	using NUnit.Framework;

	using Apache.Avalon.Container.Test.Components;

	/// <summary>
	/// Summary description for PicoContainerExtensionTestCase.
	/// </summary>
	[TestFixture]
	public class PicoContainerExtensionTestCase : ContainerTestCase
	{
		[Test]
		public void DependencyComponent()
		{
			IVehicle vehicle = 
				(IVehicle) m_container.LookupManager[ typeof(IVehicle).FullName ];

			Assertion.AssertNotNull(vehicle.Engine);
			Assertion.AssertNotNull(vehicle.Radio);
		}
	}
}
