/* File:       $Id$
 * Revision:   $Revision$
 * Author:     $Author$
 * Date:       $Date$
 *
 * The Netarchive Suite - Software to harvest and preserve websites
 * Copyright 2004-2007 Det Kongelige Bibliotek and Statsbiblioteket, Denmark
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package dk.netarkivet.harvester.harvesting.frontier;

import dk.netarkivet.harvester.datamodel.Constants;
import dk.netarkivet.harvester.harvesting.frontier.FullFrontierReport.ReportIterator;


public class RetiredQueuesFilter extends MaxSizeFrontierReportExtract {

    @Override
    public InMemoryFrontierReport process(FrontierReport initialFrontier) {
        InMemoryFrontierReport result = new InMemoryFrontierReport(
                initialFrontier.getJobName());

        FullFrontierReport full = (FullFrontierReport) initialFrontier;
        ReportIterator iter = full.iterateOnSpentBudget();
        try {
            int addedLines = 0;
            int maxSize = getMaxSize();
            while (addedLines <= maxSize && iter.hasNext()) {
                FrontierReportLine l = iter.next();

                long totalBudget = l.getTotalBudget();
                long totalSpent = l.getTotalSpend();
                long currentSize = l.getCurrentSize();
                if (totalBudget != Constants.HERITRIX_MAXOBJECTS_INFINITY
                        && currentSize > 0 && totalSpent >= totalBudget) {
                    result.addLine(new FrontierReportLine(l));
                    addedLines++;
                }
            }
        } finally {
            if (iter != null) {
                iter.close();
            }
        }

        return result;
    }

}
