#
#
#
SHELL := /bin/bash

all:
	@echo all

clean:
	echo rm -rf tmp/*

#
# misc-lib relate targets
#

# to make project independent from misc-lib, copy files from misc-lib
copy-misc-lib-files:
	cp ../misc-lib/tmp/build/jar/misc-lib.jar data/jar
	cp ../misc-lib/data/jar/*                 data/jar
	cp ../misc-lib/data/market/*              data/market

build-misc-lib:
	pushd ../misc-lib/; ant build ; popd; make copy-misc-lib-files

save-ods:
	cp ~/Dropbox/Trade/dividend-etf.ods     ~/Dropbox/Trade/SAVE/dividend-etf_$$(date +%Y%m%d).ods


#
# stock.csv
#
tmp/data/stock.csv:
ifneq (,$(wildcard tmp/data/stock.csv))
	rm -f tmp/data/stock-OLD.csv
	cp -p tmp/data/stock.csv     tmp/data/stock-OLD.csv
endif
	ant update-stock
	cp tmp/data/stock.csv ~/Dropbox/Trade/stock-jp.csv

update-stock: tmp/data/stock.csv


#
# stock-info.csv price/*.csv
#
tmp/data/stock-info.csv:
ifneq (,$(wildcard tmp/data/price))
	rm -rf tmp/data/price-OLD
	cp -rp tmp/data/price      tmp/data/price-OLD
endif
ifneq (,$(wildcard tmp/data/stock-info.csv))
	rm -f tmp/data/stock-info-OLD.csv
	cp -p tmp/data/stock-info.csv  tmp/data/stock-info-OLD.csv
endif
	ant update-stock-info-price

update-stock-info-price: tmp/data/stock-info.csv



#
# download.touch
#   "ant download" download file, it will touch tmp/data/download.touch
#
download:
	ant download

#
# reit-report.csv
#
tmp/data/reit-report.csv: tmp/data/download.touch
ifneq (,$(wildcard tmp/data/reit-report.csv))
	rm -f tmp/data/reit-report-OLD.csv
	cp -p tmp/data/reit-report.csv     tmp/data/reit-report-OLD.csv
endif
	ant update-reit-report

update-reit-report: tmp/data/reit-report.csv


#
# stock-report.csv
#
tmp/data/stock-report.csv: tmp/data/download.touch
ifneq (,$(wildcard tmp/data/stock-report.csv))
	rm -f tmp/data/stock-report-OLD.csv
	cp -p tmp/data/stock-report.csv     tmp/data/stock-report-OLD.csv
endif
	ant update-stock-report


#
# dividend-etf.csv
#
tmp/data/dividend-etf.csv: ~/Dropbox/Trade/dividend-etf.ods
ifneq (,$(wildcard tmp/data/dividend-etf.csv))
	rm -f tmp/data/dividend-etf-OLD.csv
	cp -p tmp/data/dividend-etf.csv     tmp/data/dividend-etf-OLD.csv
endif
	ant update-dividend-etf

update-dividend-etf: tmp/data/dividend-etf.csv


#
# dividend-reit.csv
#
tmp/data/dividend-reit.csv: tmp/data/reit-report.csv
ifneq (,$(wildcard tmp/data/dividend-reit.csv))
	rm -f tmp/data/dividend-reit-OLD.csv
	cp -p tmp/data/dividend-reit.csv     tmp/data/dividend-reit-OLD.csv
endif
	ant update-dividend-reit

update-dividend-reit: tmp/data/dividend-reit.csv


#
# dividend-stock.csv
#
tmp/data/dividend-stock.csv: tmp/data/stock-info.csv
ifneq (,$(wildcard tmp/data/dividend-stock.csv))
	rm -f tmp/data/dividend-stock-OLD.csv
	cp -p tmp/data/dividend-stock.csv     tmp/data/dividend-stock-OLD.csv
endif
	ant update-dividend-stock

update-dividend-stock: tmp/data/dividend-stock.csv


#
# dividend.touch
#
tmp/data/dividend.touch: tmp/data/dividend-etf.csv tmp/data/dividend-reit.csv tmp/data/dividend-stock.csv
ifneq (,$(wildcard tmp/data/dividend))
	rm -rf tmp/data/dividend-OLD
	mv     tmp/data/dividend      tmp/data/dividend-OLD
	mkdir  tmp/data/dividend
endif
	ant update-dividend
	touch tmp/data/dividend.touch

update-dividend: tmp/data/dividend.touch


#
# dividend-annual.csv
#
tmp/data/dividend-annual.csv: tmp/data/dividend-etf.csv tmp/data/dividend-reit.csv tmp/data/dividend-stock.csv
ifneq (,$(wildcard tmp/data/dividend-annual.csv))
	rm -f tmp/data/dividend-annual-OLD.csv
	cp -p tmp/data/dividend-annual.csv     tmp/data/dividend-annual-OLD.csv
endif
	ant update-dividend-annual

update-dividend-annual: tmp/data/dividend-annual.csv


#
# stats.csv
#
tmp/data/stats.csv: tmp/data/dividend-annual.csv
ifneq (,$(wildcard tmp/data/stats.csv))
	rm -rf tmp/data/stats-OLD.csv
	mv     tmp/data/stats.csv      tmp/data/stats-OLD.csv
endif
	ant update-stats
	cp tmp/data/stats.csv ~/Dropbox/Trade/stats-jp.csv

update-stats: tmp/data/stats.csv


#
# Taxonomy file
#
download-taxonomy-file:
	cd tmp/download; rm -f 61_taxonomy.zip;       wget https://www.jpx.co.jp/equities/listing/xbrl/tvdivq00000088ai-att/61_taxonomy.zip
	cd tmp/download; rm -f tse-cg-2015-04-01.zip; wget https://www.jpx.co.jp/equities/listing/xbrl/tvdivq00000088ai-att/tse-cg-2015-04-01.zip

unpack-taxonomy-file:
	cd tmp/data; rm -rf 61_taxonomy; unzip ../download/61_taxonomy.zip
	cd tmp/data/61_taxonomy; unzip tse-at-2014-01-12.zip; rm -f tse-at-2014-01-12.zip
	cd tmp/data/61_taxonomy; unzip tse-ed-2014-01-12.zip; rm -f tse-ed-2014-01-12.zip
	cd tmp/data/61_taxonomy; unzip tse-re-2014-01-12.zip; rm -f tse-re-2014-01-12.zip
	cd tmp/data/61_taxonomy; 

	cd tmp/data; rm -rf tse-cg-2015-04-01; unzip ../download/tse-cg-2015-04-01.zip
	
	