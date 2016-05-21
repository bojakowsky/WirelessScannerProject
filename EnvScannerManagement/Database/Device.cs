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
        public string Id { get; set; }

        public virtual ICollection<General> General { get; set; }
    }

    public struct Frequency
    {
        public int frequency;
        public int numberOfUses;
    }

    public class DTODevice
    {
        public DTODevice()
        {
            frequencies = new HashSet<Frequency>();
        }
        public List<string> Id;

        public int sumOfWifisCapture { get; set; }
        public int sumOfBluetoothCapture { get; set; }
        public int totalCaptures { get; set; } 

        public HashSet<Frequency> frequencies { get; set; }

        public int uniqueMACsCount { get; set; }

        public int uniqueBSSIDCount { get; set; }

        public int numberOfOpenWifis { get; set; }
       
    }

}



