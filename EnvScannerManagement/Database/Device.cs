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

        public virtual List<General> General { get; set; }
    }
}



