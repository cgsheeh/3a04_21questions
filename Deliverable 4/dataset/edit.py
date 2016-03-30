from csv import reader, writer
import re
# new = []
# for line in reader(open('us_wiki.txt', 'r')):
#     new.append(','.join(['"{0}"'.format(e) for i, e in enumerate(line) if i in [1, 3, 8]]))
#     new[-1] += '\n'
# with open('out.txt', 'w') as f:
#     f.writelines(new)



# new = []
# for line in reader(open('out.txt', 'r')):
#     line[1] = line[1].replace(',', '')
#     new.append(','.join(['"{0}"'.format(e) for i, e in enumerate(line)])
#     new[-1] += '\n'

# with open('2out.txt', 'w') as f:
#     f.writelines(new)
# new = []
# for line in reader(open('2out.txt', 'r')):
#     line[2] = line[2].split('/')[-1]
#     new.append(line)

# with open('3out.txt', 'w') as outfile:
#     csv_writer = writer(outfile)
#     csv_writer.writerows(new)


# new = []
# for line in reader(open('3out.txt', 'r')):
#     line[2] = re.sub(r'[(].*[)]', '', line[2])
#     almost_fixed = line[2].rstrip().lstrip().split(' ')
#     lat = almost_fixed[0][:-1]
#     lon = almost_fixed[1][:-1]
#     new.append([line[0], line[1], lat, lon])

# with open('out.txt', 'w') as f:
#     w = writer(f)
#     w.writerows(new)
# new = []
# with open('formatted_out.txt', 'w') as f:
#     for line in open('ca_out.txt', 'r'):
#         sp = line.split(',')
#         sp[0] = '"{0}"'.format(sp[0])
#         new.append(','.join(sp))
#     f.writelines(new)

with open('formatted_out.txt', 'r') as r:
    lines = r.readlines()
    quers = map(lambda l: 'insert into LocationExpertTable values ({0});\n'.format(l[:-1]), lines)

with open('final_out.txt', 'w') as f:
    f.writelines(quers)
