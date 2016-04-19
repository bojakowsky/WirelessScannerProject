using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Database
{
    public class DatabaseContext : DbContext
    {

        public DatabaseContext() : base("WirelessCollector")
        {
        }
        public DbSet<Device> Devices { get; set; }
        public DbSet<Bluetooth> Bluetooths { get; set; }
        public DbSet<Wifi> Wifis { get; set; }
    }
}
