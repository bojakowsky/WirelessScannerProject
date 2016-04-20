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
}



