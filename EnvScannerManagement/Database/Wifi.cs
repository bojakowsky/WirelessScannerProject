﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Database
{
    public class Wifi
    {
        public int Id { get; set; }
        public string BSSID { get; set; }
        public string Security { get; set; }
        public string SSID { get; set; }
        public int Frequency { get; set; }
        public int Level { get; set; }
        public string Timestamp { get; set; }

        public int GeneralId { get; set; }
        public virtual General General { get; set; }
    }

    public class DTOWifi
    {
        public string BSSID { get; set; }
        public string Security { get; set; }
        public string SSID { get; set; }
        public int Frequency { get; set; }
        public int Level { get; set; }
        public string Timestamp { get; set; }
        public int GeneralId { get; set; }
    }
}
