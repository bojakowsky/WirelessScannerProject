using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(EnvScannerManagement.Startup))]
namespace EnvScannerManagement
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
