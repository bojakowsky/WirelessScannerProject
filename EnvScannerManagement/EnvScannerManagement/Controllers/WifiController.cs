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
using System.Reflection;

namespace EnvScannerManagement.Controllers
{
    [Authorize(Roles = "OkUser, Admin")]
    public class WifiController : Controller
    {

        class ItemComparer : IEqualityComparer<DTOWifi>
        {
            public bool Equals(DTOWifi x, DTOWifi y)
            {
                return x.BSSID != y.BSSID;
            }

            public int GetHashCode(DTOWifi x)
            {
                return x.GeneralId;
            }
        }

        private DatabaseContext db = new DatabaseContext();
        private IQueryable<Wifi> FilterSearchValue(string searchValue, IQueryable<Wifi> wifis)
        {

            long searchLongValue;
            long.TryParse(searchValue, out searchLongValue);

            if (searchLongValue != 0 || (searchValue.Equals("0") && searchLongValue == 0))
            {
                wifis = wifis.Where(x => x.GeneralId == searchLongValue ||
                x.Id == searchLongValue ||
                x.Level == searchLongValue ||
                x.Frequency == searchLongValue);
            }
            else {
                wifis = wifis.Where(x =>
                    x.BSSID.Contains(searchValue) ||
                    x.Security.Contains(searchValue) ||
                    x.SSID.Contains(searchValue) ||
                    x.Timestamp.Contains(searchValue));
            }

            return wifis;
        }


        public async Task<ActionResult> OpenWifis(string searchValue = "") 
        {
            if (searchValue == null)
                return RedirectToAction("OpenWifis");
            var openWifis = db.Wifis.Include(w => w.General).Where(x=>x.Security.Equals("[ESS]")).AsQueryable();
            openWifis = FilterSearchValue(searchValue, openWifis);
            var openWifisList  = openWifis.AsEnumerable().GroupBy(x => x.BSSID).Where(g => g.Count() == 1).Select(g => g.First());
            return View(openWifisList.Take(1000).ToList());
        }

        // GET: Wifis
        public async Task<ActionResult> Index(string searchValue = "")
        {
            if (searchValue == null)
                return RedirectToAction("Index");
            var wifis = db.Wifis.Include(w => w.General).AsQueryable();
            wifis = FilterSearchValue(searchValue, wifis);
            return View(await wifis.OrderByDescending(x => x.Id).Take(1000).ToListAsync());
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
