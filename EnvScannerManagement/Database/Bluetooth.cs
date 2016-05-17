using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Database
{
    public class Bluetooth
    {
        public int Id { get; set; }
        public string MAC { get; set; }
        public string DeviceName { get; set; }

        public int GeneralId { get; set; }
        public virtual General General { get; set; }
    }

    public class DTOBluetooth
    {
        public string MAC { get; set; }
        public string DeviceName { get; set; }
        public int GeneralId { get; set; }
    }
}
