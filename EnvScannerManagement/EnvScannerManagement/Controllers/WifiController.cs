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
        private DatabaseContext db = new DatabaseContext();

        // GET: Wifis
        public async Task<ActionResult> Index(string searchValue = "")
        {
            if (searchValue == null)
                return RedirectToAction("Index");
            var wifis = db.Wifis.Include(w => w.General).AsQueryable();

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
