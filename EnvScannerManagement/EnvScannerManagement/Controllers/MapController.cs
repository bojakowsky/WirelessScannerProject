using Database;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;

namespace EnvScannerManagement.Controllers
{
    

    public class MapController : Controller
    {

        private DatabaseContext db = new DatabaseContext();

        // GET: Generals
        public async Task<ActionResult> Index()
        {
            db.Configuration.ProxyCreationEnabled = false;
            var generals = db.Generals;
            return View(await generals.AsNoTracking().OrderByDescending(x => x.DateAndTime).ToListAsync());
        }

        struct WifiAndBluetoothMerged
        {
            public List<DTOBluetooth> bt;
            public List<DTOWifi> wifi;
        }

        public async Task<ActionResult> GetDetails(int generalId)
        {
            var wifiDetails = await db.Wifis.AsNoTracking().Where(y => y.GeneralId == generalId).Select(x => new DTOWifi()
            {
                BSSID = x.BSSID,
                Frequency = x.Frequency,
                Level = x.Level,
                Security = x.Security,
                SSID = x.SSID,
                Timestamp = x.Timestamp
            }).ToListAsync();

            var bluetoothDetails = await db.Bluetooths.AsNoTracking().Where(y => y.GeneralId == generalId).Select(x => new DTOBluetooth()
            {
                DeviceName = x.DeviceName,
                MAC = x.MAC
            }).ToListAsync();

            var merged = new WifiAndBluetoothMerged()
            {
                bt = bluetoothDetails,
                wifi = wifiDetails
            };
            
            return Json(merged, JsonRequestBehavior.AllowGet);
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