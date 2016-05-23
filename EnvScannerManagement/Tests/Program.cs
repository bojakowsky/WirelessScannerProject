using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tests
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine(DateTime.Now.ToString().Contains("5/23/2016 "));

            long searchLongValue;
            string searchValue = "5/22";
            long.TryParse(searchValue, out searchLongValue);

            if (searchLongValue != 0 || (searchValue.Equals("0") && searchLongValue == 0))
            {
                Console.WriteLine("If");
            }
            else {
                Console.WriteLine("5/22/2016 4:02:19 PM".Contains(searchValue));
            }

            Console.ReadKey();
        }
    }
}
