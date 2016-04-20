using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Data.Entity;
using System.Data.Entity.Infrastructure.Annotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Database
{
    public class DatabaseContext : DbContext
    {
       
        public DatabaseContext() : base("WirelessCollector")
        {

            this.Database.CreateIfNotExists();
        }

        public DbSet<General> Generals { get; set; }
        public DbSet<Device> Devices { get; set; }
        public DbSet<Bluetooth> Bluetooths { get; set; }
        public DbSet<Wifi> Wifis { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            //Device
            modelBuilder.Entity<Device>().HasKey(x => x.Id);

            //Bluetooth
            modelBuilder.Entity<Bluetooth>().HasKey(x => x.Id);

            //Wifi
            modelBuilder.Entity<Wifi>().HasKey(x => x.Id);

            //General
            modelBuilder.Entity<General>().HasKey(x => x.Id);

            //one-to-many
            modelBuilder.Entity<General>()
                .HasRequired<Device>(s => s.Device)
                .WithMany(s => s.General)
                .HasForeignKey(s => s.DeviceId)
                .WillCascadeOnDelete();

            //one-to-one 
            modelBuilder.Entity<Wifi>()
                .HasRequired<General>(s => s.General);
            //one-to-one
            modelBuilder.Entity<Bluetooth>()
                .HasRequired<General>(s => s.General);
        }
    }
}
