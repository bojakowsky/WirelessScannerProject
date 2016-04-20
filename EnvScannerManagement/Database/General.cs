using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Database
{
    public class General
    {
        public int Id { get; set; }
        //General data
        public string AndroidAPI { get; set; }
        public int NumberOfWifiConnections { get; set; }
        public int NumberOfBtConnections { get; set; }
        public double GPSlatitude { get; set; }
        public double GPSLongtitude { get; set; }
        public DateTime DateAndTime { get; set; }

        //Device
        public string DeviceId { get; set; }
        public virtual Device Device { get; set; }

        //Wifi data
        public virtual ICollection<Wifi> Wifis { get; set; }

        //Bluetooth
        public virtual ICollection<Bluetooth> Bluetooths { get; set; }
    }
}
