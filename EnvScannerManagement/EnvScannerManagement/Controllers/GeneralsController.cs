using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Database;
using System.Globalization;

namespace EnvScannerManagement.Controllers
{

    [Authorize(Roles = "OkUser, Admin")]
    public class GeneralsController : Controller
    {
        private DatabaseContext db = new DatabaseContext();

        // GET: Generals
        public async Task<ActionResult> Index(string searchValue = "")
        {
            if (searchValue == null)
                return RedirectToAction("Index");
            var generals = db.Generals.AsQueryable();

            long searchLongValue;
            long.TryParse(searchValue, out searchLongValue);
            if (searchLongValue != 0 || (searchValue.Equals("0") && searchLongValue == 0))
            {
                generals = generals.Where(x => x.NumberOfWifiConnections == searchLongValue ||
                    x.Id == searchLongValue ||
                    x.NumberOfBtConnections == searchLongValue ||
                    (long)x.GPSLongtitude == searchLongValue ||
                    (long)x.GPSlatitude == searchLongValue);
            }
            else
            {
                generals = generals.Where(x =>
                    x.AndroidAPI.Contains(searchValue) ||
                    (
                        x.DateAndTime.Month.ToString() 
                        + "/" + x.DateAndTime.Day.ToString() 
                        + "/" + x.DateAndTime.Year + " "
                        + x.DateAndTime.Hour
                        + ":" + x.DateAndTime.Minute.ToString()
                        + ":" + x.DateAndTime.Second.ToString()
                    ).Contains(searchValue) ||
                    x.DeviceId.Contains(searchValue));
            }
            return View(await generals.OrderByDescending(x => x.Id).Take(1000).ToListAsync());
            //return View(new List<General>() { new General() { AndroidAPI = db.Generals.Select(x=> buildDateTimeFromSQL(x.DateAndTime)).First() } });
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}
