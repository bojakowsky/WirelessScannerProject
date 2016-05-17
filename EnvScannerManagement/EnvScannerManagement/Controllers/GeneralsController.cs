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
    public class GeneralsController : Controller
    {
        private DatabaseContext db = new DatabaseContext();

        // GET: Generals

        public async Task<ActionResult> Index()
        {
            var generals = db.Generals.Include(g => g.Device);
            return View(await generals.OrderByDescending(x => x.Id).Take(1000).ToListAsync());
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
