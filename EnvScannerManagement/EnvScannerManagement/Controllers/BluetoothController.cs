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

namespace EnvScannerManagement.Controllers
{
    [Authorize(Roles = "OkUser, Admin")]
    public class BluetoothController : Controller
    {
        private DatabaseContext db = new DatabaseContext();

        // GET: Bluetooth
        public async Task<ActionResult> Index(string searchValue = "")
        {
            if (searchValue == null)
                return RedirectToAction("Index");
            var bts = db.Bluetooths.Include(w => w.General).AsQueryable();

            long searchLongValue;
            long.TryParse(searchValue, out searchLongValue);

            if (searchLongValue != 0 || (searchValue.Equals("0") && searchLongValue == 0))
            {
                bts = bts.Where(x => x.GeneralId == searchLongValue ||
                x.Id == searchLongValue);
            }
            else {
                bts = bts.Where(x =>
                    x.DeviceName.Contains(searchValue) ||
                    x.MAC.Contains(searchValue) || 
                    x.General.DeviceId.Contains(searchValue));
            }
            return View(await bts.OrderByDescending(x => x.Id).Take(1000).ToListAsync());
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
