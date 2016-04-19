using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Database
{
    public class Wifi
    {
        public string BSSID { get; set; }
        public string Security { get; set; }
        public string SSID { get; set; }
        public int Frequency { get; set; }
        public int Level { get; set; }
        public string Timestamp { get; set; }

        public string DeviceId { get; set; }
        public virtual Device Device { get; set; }
    }
}
