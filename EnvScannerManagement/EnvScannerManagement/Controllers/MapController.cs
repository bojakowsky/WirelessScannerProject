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
            var generals = db.Generals;
            return View(await generals.OrderByDescending(x=>x.DateAndTime).Take(50).ToListAsync());
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