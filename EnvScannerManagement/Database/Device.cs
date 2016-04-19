using Database;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Database
{
    public class Device
    {
        //Device data
        public string DeviceId { get; set; }
        public string AndroidAPI { get; set; }
        public int NumberOfWifiConnections { get; set; }
        public int NumberOfBtConnections { get; set; }
        public double GPSlatitude { get; set; }
        public double GPSLongtitude { get; set; }

        //Wifi data
        public virtual List<Wifi> Wifis { get; set; }

        //Bluetooth
        public virtual List<Bluetooth> Bluetooths { get; set; }
    }
}



